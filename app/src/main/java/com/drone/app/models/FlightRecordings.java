package com.drone.app.models;

import java.util.List;

public class FlightRecordings {

    private String id;

    private List<Double> motor_1_temps;
    private List<Double> motor_2_temps;
    private List<Double> motor_3_temps;
    private List<Double> motor_4_temps;
    private List<Double> humiditys;
    private List<Double> battery_temps;
    private List<Double> altitudes;

    private Long timestamp;

    public FlightRecordings(String id, List<Double> motor1, List<Double> motor2, List<Double> motor3, List<Double> motor4, List<Double> humid, List<Double> batteries, List<Double> altitudes, Long time) {
        this.id = id;
        this.motor_1_temps = motor1;
        this.motor_2_temps = motor2;
        this.motor_3_temps = motor3;
        this.motor_4_temps = motor4;
        this.humiditys = humid;
        this.battery_temps=batteries;
        this.altitudes=altitudes;
        this.timestamp = time;
    }

    public String getId() {
        return id;
    }

    public List<Double> getMotor_1_temps() {
        return motor_1_temps;
    }

    public List<Double> getMotor_2_temps() {
        return motor_2_temps;
    }

    public List<Double> getMotor_3_temps() {
        return motor_3_temps;
    }

    public List<Double> getMotor_4_temps() {
        return motor_4_temps;
    }

    public List<Double> getHumiditys() {
        return humiditys;
    }

    public List<Double> getBattery_temps() {
        return battery_temps;
    }

    public List<Double> getAltitudes() {
        return altitudes;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
