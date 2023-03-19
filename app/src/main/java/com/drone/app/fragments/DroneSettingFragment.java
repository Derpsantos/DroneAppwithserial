package com.drone.app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.drone.app.R;
import com.drone.app.models.ComponentUsage;
import com.drone.app.utility.DatabaseHelper;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    //For testing

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

        //database.add_components(UUID.randomUUID().toString(), 180000005, 1800000066,180000005,166666,1456456,System.currentTimeMillis());
        return view;

    }

    @Override
    public void onStart(){
        super.onStart();
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
            public void onClick(View v) {
            //database.getAllComps(this::update_component_usage);
                database.getAllComps(this::resetm1);

            }

            private void resetm1(List<ComponentUsage> comp) {
                long temp2= comp.get(comp.size()-1).getMotor2_time();
                long temp3= comp.get(comp.size()-1).getMotor3_time();
                long temp4= comp.get(comp.size()-1).getMotor4_time();
                long tempb= comp.get(comp.size()-1).getBattery_time();
                database.add_components(UUID.randomUUID().toString(),0,temp2, temp3,temp4,tempb, System.currentTimeMillis());

                }
            //For testing

        });
        m2reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {database.getAllComps(this::resetm2);}

            private void resetm2(List<ComponentUsage> comp) {
                long temp1= comp.get(comp.size()-1).getMotor1_time();
                long temp3= comp.get(comp.size()-1).getMotor3_time();
                long temp4= comp.get(comp.size()-1).getMotor4_time();
                long tempb= comp.get(comp.size()-1).getBattery_time();
                database.add_components(UUID.randomUUID().toString(),temp1,0, temp3,temp4,tempb, System.currentTimeMillis());
            }});

        m3reset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){database.getAllComps(this::resetm3);}

            private void resetm3(List<ComponentUsage> comp) {
                long temp1= comp.get(comp.size()-1).getMotor1_time();
                long temp2= comp.get(comp.size()-1).getMotor2_time();
                long temp4= comp.get(comp.size()-1).getMotor4_time();
                long tempb= comp.get(comp.size()-1).getBattery_time();
                database.add_components(UUID.randomUUID().toString(),temp1,temp2, 0,temp4,tempb, System.currentTimeMillis());
            }});

        m4reset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){database.getAllComps(this::resetm4);}

            private void resetm4(List<ComponentUsage> comp) {

                long temp1= comp.get(comp.size()-1).getMotor1_time();
                long temp2= comp.get(comp.size()-1).getMotor2_time();
                long temp3= comp.get(comp.size()-1).getMotor3_time();
                long tempb= comp.get(comp.size()-1).getBattery_time();
                database.add_components(UUID.randomUUID().toString(),temp1,temp2, temp3,0,tempb, System.currentTimeMillis());

            }});

        breset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){database.getAllComps(this::resetb);}
            private void resetb(List<ComponentUsage> comp) {

                long temp1= comp.get(comp.size()-1).getMotor1_time();
                long temp2= comp.get(comp.size()-1).getMotor2_time();
                long temp3= comp.get(comp.size()-1).getMotor3_time();
                long temp4= comp.get(comp.size()-1).getMotor4_time();
                database.add_components(UUID.randomUUID().toString(),temp1,temp2, temp3,temp4,0, System.currentTimeMillis());

            }});
        setall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.add_components(UUID.randomUUID().toString(), 0, 0, 0, 0, 0, System.currentTimeMillis());

            }});
        database.getAllComps(this::getdata);

    }

    private void getdata(List<ComponentUsage> comp){
        m1time=comp.get(comp.size()-1).getMotor1_time();
        m2time=comp.get(comp.size()-1).getMotor2_time();
        m3time=comp.get(comp.size()-1).getMotor3_time();
        m4time=comp.get(comp.size()-1).getMotor4_time();
        btime=comp.get(comp.size()-1).getBattery_time();
        String t1 = convert_time(m1time);
        String t2 = convert_time(m2time);
        String t3 = convert_time(m3time);
        String t4 = convert_time(m4time);
        String battery = convert_time(btime);

        m1.setText("Motor 1 time used: " + t1 );
        if(m1time>180000000){m1.append(" Motor 1 has exceeded recommended usage time, please replace as soon as possible");}
        m2.setText("Motor 2 time used: " + t2 );
        if(m2time>180000000){m2.append(" Motor 2 has exceeded recommended usage time, please replace as soon as possible");}
        m3.setText("Motor 3 time used: " + t3 );
        if(m3time>180000000){m3.append(" Motor 3 has exceeded recommended usage time, please replace as soon as possible");}
        m4.setText("Motor 4 time used: " + t4 );
        if(m4time>180000000){m4.append(" Motor 4 has exceeded recommended usage time, please replace as soon as possible");}
        b.setText("Battery time used: " + battery );
        if(btime>180000000){b.append(" Battery has exceeded recommended usage time, please replace as soon as possible");}
    }

    private String convert_time(long time){
        String s = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(time),
                TimeUnit.MILLISECONDS.toMinutes(time) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
                TimeUnit.MILLISECONDS.toSeconds(time) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
        return s;
    }



}