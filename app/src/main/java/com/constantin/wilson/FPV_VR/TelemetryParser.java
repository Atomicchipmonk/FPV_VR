package com.constantin.wilson.FPV_VR;

/**
 * Created by heisty on 4/19/17.
 */

public interface TelemetryParser {

    void stop();
    void start();
    float getVoltage();
    float getAmpere();
    float getBaroAltitude();
    float getAltitude();
    float getLongitude();
    float getLatitude();
    float getSpeed();
    float getRoll();
    float getPitch();
    float getYaw();
    float getRSSI();


}
