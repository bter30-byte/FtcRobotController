package com.qualcomm.robotcore.hardware;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Simulator copy of the FTC SDK hardware map: finds devices by the names in the robot
 * configuration. The simulator's robot has: left_drive, right_drive, arm (DcMotor),
 * claw (Servo), touch_sensor (TouchSensor), sensor_color (ColorSensor / NormalizedColorSensor).
 */
public class HardwareMap implements Iterable<HardwareDevice> {

    /** Older style of finding devices, for example hardwareMap.dcMotor.get("left_drive"). */
    public class DeviceMapping<D extends HardwareDevice> implements Iterable<D> {
        private final Class<D> type;

        DeviceMapping(Class<D> type) {
            this.type = type;
        }

        public D get(String deviceName) {
            return HardwareMap.this.get(type, deviceName);
        }

        @Override
        public Iterator<D> iterator() {
            return getAll(type).iterator();
        }
    }

    public DeviceMapping<DcMotor> dcMotor = new DeviceMapping<DcMotor>(DcMotor.class);
    public DeviceMapping<Servo> servo = new DeviceMapping<Servo>(Servo.class);
    public DeviceMapping<TouchSensor> touchSensor = new DeviceMapping<TouchSensor>(TouchSensor.class);
    public DeviceMapping<ColorSensor> colorSensor = new DeviceMapping<ColorSensor>(ColorSensor.class);

    private final Map<String, HardwareDevice> devices = new LinkedHashMap<String, HardwareDevice>();

    public HardwareMap() {
    }

    public <T> T get(Class<? extends T> classOrInterface, String deviceName) {
        T result = tryGet(classOrInterface, deviceName);
        if (result == null) {
            throw new IllegalArgumentException(String.format(
                    "Unable to find a hardware device with name \"%s\" and type %s",
                    deviceName, classOrInterface.getSimpleName()));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> T tryGet(Class<? extends T> classOrInterface, String deviceName) {
        HardwareDevice device = devices.get(deviceName == null ? null : deviceName.trim());
        if (device != null && classOrInterface.isInstance(device)) {
            return (T) device;
        }
        return null;
    }

    public HardwareDevice get(String deviceName) {
        HardwareDevice device = devices.get(deviceName);
        if (device == null) {
            throw new IllegalArgumentException(String.format(
                    "Unable to find a hardware device with name \"%s\"", deviceName));
        }
        return device;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getAll(Class<? extends T> classOrInterface) {
        List<T> result = new ArrayList<T>();
        for (HardwareDevice device : devices.values()) {
            if (classOrInterface.isInstance(device)) {
                result.add((T) device);
            }
        }
        return result;
    }

    public SortedSet<String> getAllNames(Class<? extends HardwareDevice> classOrInterface) {
        SortedSet<String> names = new TreeSet<String>();
        for (Map.Entry<String, HardwareDevice> e : devices.entrySet()) {
            if (classOrInterface.isInstance(e.getValue())) {
                names.add(e.getKey());
            }
        }
        return names;
    }

    public void put(String deviceName, HardwareDevice device) {
        devices.put(deviceName, device);
    }

    public int size() {
        return devices.size();
    }

    @Override
    public Iterator<HardwareDevice> iterator() {
        return new ArrayList<HardwareDevice>(devices.values()).iterator();
    }
}
