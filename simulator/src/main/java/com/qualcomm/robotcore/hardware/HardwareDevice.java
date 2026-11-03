package com.qualcomm.robotcore.hardware;

/** Simulator copy of the FTC SDK interface shared by every device. */
public interface HardwareDevice {
    String getDeviceName();

    String getConnectionInfo();

    int getVersion();

    void resetDeviceConfigurationForOpMode();

    void close();
}
