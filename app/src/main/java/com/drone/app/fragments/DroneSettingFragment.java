package com.drone.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.drone.app.R;
import com.drone.app.models.ComponentUsage;
import com.drone.app.utility.DatabaseHelper;

public class DroneSettingFragment extends Fragment {
    DatabaseHelper database;
    private TextView m1;
    private TextView m2;
    private TextView m3;
    private TextView m4;
    private TextView b;

    private long m1time;
    private long m2time;
    private long m3time;
    private long m4time;
    private long btime;

    private Button m1reset;
    private Button m2reset;
    private Button m3reset;
    private Button m4reset;
    private Button breset;
    private Button setall;
    View view;

    public DroneSettingFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


database =new DatabaseHelper();

        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_setting, container, false);


        return view;

    }

    @Override
    public void onStart(){
        super.onStart();
        //database.initializecomponents(0,0,0,0,0);
        m1=view.findViewById(R.id.Motor_1_time);
        m2=view.findViewById(R.id.Motor_2_time);
        m3=view.findViewById(R.id.Motor_3_time);
        m4=view.findViewById(R.id.Motor_4_time);
        b=view.findViewById(R.id.Battery_time);
        m1reset=view.findViewById(R.id.m1_reset);
        m2reset=view.findViewById(R.id.m2_reset);
        m3reset=view.findViewById(R.id.m3_reset);
        m4reset=view.findViewById(R.id.m4_reset);
        breset=view.findViewById(R.id.battery_reset);
        setall=view.findViewById(R.id.initialize_button);
        m1reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {database.getcomponentusage(this::resetm1);}
            private void resetm1(ComponentUsage componentUsage) {componentUsage.setMotor1_time(0);}});
        m2reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {database.getcomponentusage(this::resetm2);}

            private void resetm2(ComponentUsage componentUsage) {componentUsage.setMotor2_time(0);}});

        m3reset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){database.getcomponentusage(this::resetm3);}

            private void resetm3(ComponentUsage componentUsage) { componentUsage.setMotor3_time(0);}});

        m4reset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){database.getcomponentusage(this::resetm4);}

            private void resetm4(ComponentUsage componentUsage) { componentUsage.setMotor4_time(0);}});

        breset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){database.getcomponentusage(this::resetb);}
            private void resetb(ComponentUsage componentUsage) {componentUsage.setBattery_time(0);}});
        setall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.initializecomponents(0,0,0,0,0);}

        });
        database.getcomponentusage(this::getdata);
        m1.setText("Motor 1 time used: " + m1time );
        m2.setText("Motor 2 time used: " + m2time );
        m3.setText("Motor 3 time used: " + m3time );
        m4.setText("Motor 4 time used: " + m4time );
        b.setText("Battery time used: " + btime );
    }

    private void getdata(ComponentUsage comp){
        m1time=comp.getMotor1_time();
        m2time=comp.getMotor2_time();
        m3time=comp.getMotor3_time();
        m4time=comp.getMotor4_time();
        btime=comp.getBattery_time();
    }

}