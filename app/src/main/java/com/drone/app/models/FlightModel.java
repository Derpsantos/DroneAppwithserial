package com.drone.app.models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class FlightModel {

    private String id;

    private double Motor1_temp_max;

    private double Motor2_temp_max;

    private double Motor3_temp_max;

    private double Motor4_temp_max;

    private double humidity_max;
    private double battery_max;
    private double altitude;
    private double latitude;
    private double longitude;

    private long timestamp;

    public FlightModel(String id, double motor1, double motor2, double motor3, double motor4, double humidity, double battery, double altitude, double latitude, double longitude, long time) {
        this.id = id;
        this.Motor1_temp_max = motor1;
        this.Motor2_temp_max = motor2;
        this.Motor3_temp_max = motor3;
        this.Motor4_temp_max = motor4;
        this.humidity_max=humidity;
        this.battery_max=battery;
        this.altitude=altitude;
        this.latitude=latitude;
        this.longitude=longitude;
        this.timestamp = time;
    }

    public String getFlightId() {
        return id;
    }

    public double getMotor1_temp_max() {
        return Motor1_temp_max;
    }

    public double getMotor2_temp_max() {
        return Motor2_temp_max;
    }

    public double getMotor3_temp_max() {
        return Motor3_temp_max;
    }

    public double getMotor4_temp_max() {
        return Motor4_temp_max;
    }

    public double getHumidity_max() {
        return humidity_max;
    }

    public double getBattery_max_max() {
        return battery_max;
    }

    public double getAltitude_max() {
        return altitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    //@RequiresApi(api = Build.VERSION_CODES.0)
    public LocalDateTime getDateTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }
}
