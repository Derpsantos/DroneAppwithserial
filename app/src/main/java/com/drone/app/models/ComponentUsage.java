package com.drone.app.models;

public class ComponentUsage {
    private long Motor1_time;
    private long Motor2_time;
    private long Motor3_time;
    private long Motor4_time;
    private long battery_time;

    public ComponentUsage(long motor1_time, long motor2_time, long motor3_time, long motor4_time, long battery_time) {
        Motor1_time = motor1_time;
        Motor2_time = motor2_time;
        Motor3_time = motor3_time;
        Motor4_time = motor4_time;
        this.battery_time = battery_time;
    }

    public ComponentUsage(){}

    public long getMotor1_time() {
        return Motor1_time;
    }

    public void setMotor1_time(long motor1_time) {
        Motor1_time = motor1_time;
    }

    public long getMotor2_time() {
        return Motor2_time;
    }

    public void setMotor2_time(long motor2_time) {
        Motor2_time = motor2_time;
    }

    public long getMotor3_time() {
        return Motor3_time;
    }

    public void setMotor3_time(long motor3_time) {
        Motor3_time = motor3_time;
    }

    public long getMotor4_time() {
        return Motor4_time;
    }

    public void setMotor4_time(long motor4_time) {
        Motor4_time = motor4_time;
    }

    public long getBattery_time() {
        return battery_time;
    }

    public void setBattery_time(long battery_time) {
        this.battery_time = battery_time;
    }
}
