package robotsim;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * Finds the team's Java files in TeamCode, compiles them against the simulator's copy of the
 * FTC library, and loads the OpModes. Only these TeamCode folders are loaded:
 *   teamcode/lessons, teamcode/missions, teamcode/students
 * Anything inside a folder named "robotonly" is skipped (for code that uses hardware the
 * simulator doesn't have). Each student folder and each mission file is compiled separately,
 * so one broken file doesn't stop everyone else's programs from running.
 */
final class CodeLoader {

    static final String BASE = "org/firstinspires/ftc/teamcode";

    /** One OpMode the simulator can run. */
    static final class Entry {
        final Class<? extends OpMode> type;
        final String name;
        final String group;
        final boolean teleOp;
        final boolean disabled;

        Entry(Class<? extends OpMode> type, String name, String group, boolean teleOp, boolean disabled) {
            this.type = type;
            this.name = name;
            this.group = group;
            this.teleOp = teleOp;
            this.disabled = disabled;
        }

        @Override
        public String toString() {
            String g = group.isEmpty() ? "" : group + " › ";
            return String.format("%s%s   [%s]%s", g, name, teleOp ? "TeleOp" : "Auto",
                    disabled ? "  (hidden on robot)" : "");
        }
    }

    /** One compile error, pointing at the original file in TeamCode. */
    static final class Problem {
        final String file;
        final long line;
        final String message;
        final String code;

        Problem(String file, long line, String message, String code) {
            this.file = file;
            this.line = line;
            this.message = message;
            this.code = code;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(file);
            if (line > 0) {
                sb.append(", line ").append(line);
            }
            sb.append(":\n    ").append(message);
            if (code != null && !code.trim().isEmpty()) {
                sb.append("\n    → ").append(code.trim());
            }
            return sb.toString();
        }
    }

    static final class Result {
        final List<Entry> entries;
        final List<Problem> problems;
        final String fatal;

        Result(List<Entry> entries, List<Problem> problems, String fatal) {
            this.entries = entries;
            this.problems = problems;
            this.fatal = fatal;
        }
    }

    private final File javaRoot;
    private long lastFingerprint = Long.MIN_VALUE;

    CodeLoader(File javaRoot) {
        this.javaRoot = javaRoot;
    }

    File root() {
        return javaRoot;
    }

    /** Finds TeamCode/src/main/java from a system property or by looking around the folders. */
    static File locateTeamCode() {
        String prop = System.getProperty("robotsim.teamcode");
        if (prop != null && new File(prop).isDirectory()) {
            return new File(prop);
        }
        File dir = new File(System.getProperty("user.dir")).getAbsoluteFile();
        for (int i = 0; i < 4 && dir != null; i++) {
            File candidate = new File(dir, "TeamCode/src/main/java");
            if (candidate.isDirectory()) {
                return candidate;
            }
            dir = dir.getParentFile();
        }
        return null;
    }

    /** True if any team file was added, removed, or saved since the last load. */
    boolean changedSinceLastLoad() {
        return fingerprint() != lastFingerprint;
    }

    private long fingerprint() {
        long f = 17;
        for (List<File> unit : units()) {
            for (File file : unit) {
                f = f * 31 + file.getPath().hashCode();
                f = f * 31 + file.lastModified();
                f = f * 31 + file.length();
            }
        }
        return f;
    }

    /** Groups of files that are compiled together. */
    private List<List<File>> units() {
        List<List<File>> units = new ArrayList<List<File>>();
        File base = new File(javaRoot, BASE);
        List<File> lessons = javaFiles(new File(base, "lessons"));
        if (!lessons.isEmpty()) {
            units.add(lessons);
        }
        for (String folder : new String[]{"missions", "students"}) {
            File dir = new File(base, folder);
            File[] children = dir.listFiles();
            if (children == null) {
                continue;
            }
            Arrays.sort(children);
            for (File child : children) {
                if (child.isDirectory()) {
                    List<File> files = javaFiles(child);
                    if (!files.isEmpty()) {
                        units.add(files);
                    }
                } else if (child.getName().endsWith(".java")) {
                    units.add(Collections.singletonList(child));
                }
            }
        }
        return units;
    }

    private static List<File> javaFiles(File dir) {
        List<File> out = new ArrayList<File>();
        collect(dir, out);
        Collections.sort(out);
        return out;
    }

    private static void collect(File dir, List<File> out) {
        File[] children = dir.listFiles();
        if (children == null || dir.getName().equalsIgnoreCase("robotonly")) {
            return;
        }
        for (File child : children) {
            if (child.isDirectory()) {
                collect(child, out);
            } else if (child.getName().endsWith(".java")) {
                out.add(child);
            }
        }
    }

    Result load() {
        lastFingerprint = fingerprint();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            return new Result(Collections.<Entry>emptyList(), Collections.<Problem>emptyList(),
                    "This Java has no compiler, so the simulator can't load your code. "
                            + "Start the simulator from Android Studio with the \"Robot Simulator\" run configuration.");
        }
        File outRoot;
        try {
            outRoot = Files.createTempDirectory("robotsim-classes").toFile();
            outRoot.deleteOnExit();
        } catch (IOException e) {
            return new Result(Collections.<Entry>emptyList(), Collections.<Problem>emptyList(),
                    "Couldn't create a temporary folder: " + e.getMessage());
        }

        List<File> outputs = new ArrayList<File>();
        List<Problem> problems = new ArrayList<Problem>();
        int n = 0;
        for (List<File> unit : units()) {
            File out = new File(outRoot, "unit" + (n++));
            List<Problem> unitProblems = new ArrayList<Problem>();
            if (compile(compiler, unit, out, outputs, unitProblems)) {
                outputs.add(out);
                continue;
            }
            problems.addAll(unitProblems);
            if (unit.size() > 1) {
                // Load whatever compiles on its own, so one bad file doesn't hide the rest.
                for (File single : unit) {
                    File o = new File(outRoot, "unit" + (n++));
                    if (compile(compiler, Collections.singletonList(single), o, outputs, new ArrayList<Problem>())) {
                        outputs.add(o);
                    }
                }
            }
        }
        return new Result(loadEntries(outputs), problems, null);
    }

    private boolean compile(JavaCompiler compiler, List<File> files, File outDir, List<File> extraClasspath,
                            List<Problem> problems) {
        outDir.mkdirs();
        StringBuilder cp = new StringBuilder(System.getProperty("java.class.path"));
        for (File f : extraClasspath) {
            cp.append(File.pathSeparator).append(f.getAbsolutePath());
        }
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fm = compiler.getStandardFileManager(diagnostics, Locale.ENGLISH, StandardCharsets.UTF_8);
        try {
            List<String> options = new ArrayList<String>(Arrays.asList(
                    "-d", outDir.getAbsolutePath(), "-classpath", cp.toString(),
                    "-encoding", "UTF-8", "-proc:none", "-nowarn", "-Xlint:-options"));
            // Same Java language level as the robot (TeamCode is compiled for Java 8).
            options.add("--release");
            options.add("8");
            Iterable<? extends JavaFileObject> sources = fm.getJavaFileObjectsFromFiles(files);
            boolean ok = compiler.getTask(null, fm, diagnostics, options, null, sources).call();
            for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
                if (d.getKind() == Diagnostic.Kind.ERROR) {
                    problems.add(toProblem(d));
                }
            }
            return ok;
        } catch (RuntimeException e) {
            problems.add(new Problem("(compiler)", 0, String.valueOf(e.getMessage()), null));
            return false;
        } finally {
            try {
                fm.close();
            } catch (IOException ignored) {
                // nothing to do
            }
        }
    }

    private Problem toProblem(Diagnostic<? extends JavaFileObject> d) {
        String file = "(unknown file)";
        String code = null;
        if (d.getSource() != null) {
            File f = new File(d.getSource().toUri());
            file = relative(f);
            if (d.getLineNumber() > 0) {
                try {
                    List<String> lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
                    if (d.getLineNumber() <= lines.size()) {
                        code = lines.get((int) d.getLineNumber() - 1);
                    }
                } catch (IOException ignored) {
                    // show the error without the code line
                }
            }
        }
        String message = d.getMessage(Locale.ENGLISH);
        int nl = message.indexOf('\n');
        if (nl > 0) {
            message = message.substring(0, nl) + "  (" + message.substring(nl + 1).trim().replace('\n', ' ') + ")";
        }
        return new Problem(file, d.getLineNumber(), message, code);
    }

    private String relative(File f) {
        String root = javaRoot.getAbsolutePath();
        String path = f.getAbsolutePath();
        String rel = path.startsWith(root) ? path.substring(root.length() + 1) : path;
        return "TeamCode/.../" + rel.replace('\\', '/').replace(BASE + "/", "");
    }

    @SuppressWarnings("unchecked")
    private List<Entry> loadEntries(List<File> outputs) {
        List<Entry> entries = new ArrayList<Entry>();
        if (outputs.isEmpty()) {
            return entries;
        }
        try {
            URL[] urls = new URL[outputs.size()];
            for (int i = 0; i < urls.length; i++) {
                urls[i] = outputs.get(i).toURI().toURL();
            }
            URLClassLoader loader = new URLClassLoader(urls, CodeLoader.class.getClassLoader());
            for (File out : outputs) {
                List<File> classFiles = new ArrayList<File>();
                collectClasses(out, classFiles);
                for (File cf : classFiles) {
                    String rel = cf.getAbsolutePath().substring(out.getAbsolutePath().length() + 1);
                    String className = rel.substring(0, rel.length() - ".class".length())
                            .replace(File.separatorChar, '.');
                    Class<?> c;
                    try {
                        c = Class.forName(className, false, loader);
                    } catch (Throwable t) {
                        continue;
                    }
                    if (!OpMode.class.isAssignableFrom(c) || Modifier.isAbstract(c.getModifiers())
                            || !Modifier.isPublic(c.getModifiers())) {
                        continue;
                    }
                    TeleOp t = c.getAnnotation(TeleOp.class);
                    Autonomous a = c.getAnnotation(Autonomous.class);
                    if (t == null && a == null) {
                        continue;
                    }
                    String name = t != null ? t.name() : a.name();
                    String group = t != null ? t.group() : a.group();
                    if (name.isEmpty()) {
                        name = c.getSimpleName();
                    }
                    entries.add(new Entry((Class<? extends OpMode>) c, name, group, t != null,
                            c.getAnnotation(Disabled.class) != null));
                }
            }
        } catch (IOException e) {
            // fall through with what we have
        }
        Collections.sort(entries, new Comparator<Entry>() {
            @Override
            public int compare(Entry a, Entry b) {
                int r = Integer.compare(rank(a.group), rank(b.group));
                if (r != 0) {
                    return r;
                }
                r = a.group.compareToIgnoreCase(b.group);
                return r != 0 ? r : a.name.compareToIgnoreCase(b.name);
            }

            private int rank(String group) {
                if (group.equalsIgnoreCase("Missions")) {
                    return 0;
                }
                return group.equalsIgnoreCase("Lessons") ? 2 : 1;
            }
        });
        return entries;
    }

    private static void collectClasses(File dir, List<File> out) {
        File[] children = dir.listFiles();
        if (children == null) {
            return;
        }
        for (File c : children) {
            if (c.isDirectory()) {
                collectClasses(c, out);
            } else if (c.getName().endsWith(".class")) {
                out.add(c);
            }
        }
    }
}
