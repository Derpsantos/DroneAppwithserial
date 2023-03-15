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

    public FlightRecordings(){}

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

    public void setId(String id) {
        this.id = id;
    }

    public void setMotor_1_temps(List<Double> motor_1_temps) {
        this.motor_1_temps = motor_1_temps;
    }

    public void setMotor_2_temps(List<Double> motor_2_temps) {
        this.motor_2_temps = motor_2_temps;
    }

    public void setMotor_3_temps(List<Double> motor_3_temps) {
        this.motor_3_temps = motor_3_temps;
    }

    public void setMotor_4_temps(List<Double> motor_4_temps) {
        this.motor_4_temps = motor_4_temps;
    }

    public void setHumiditys(List<Double> humiditys) {
        this.humiditys = humiditys;
    }

    public void setBattery_temps(List<Double> battery_temps) {
        this.battery_temps = battery_temps;
    }

    public void setAltitudes(List<Double> altitudes) {
        this.altitudes = altitudes;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
