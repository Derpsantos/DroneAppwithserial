package com.drone.app.fragments;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.drone.app.MainActivity;
import com.drone.app.R;
import com.drone.app.adapters.FlightListHandler;
import com.drone.app.models.FlightModel;
import com.drone.app.utility.DatabaseHelper;
import com.drone.app.utility.RecyclerItemClickListener;
import com.drone.app.utility.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class DroneConditionFragment extends Fragment {
    View view;

    List<FlightModel> flights;
    DatabaseHelper database;
    RecyclerView list;


    Context thiscontext;
    boolean toggle;

    CountDownTimer countDowntimer;


    public DroneConditionFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new DatabaseHelper();
        Date date = new Date(System.currentTimeMillis());
        Long timestamp = datetoTimestamp(date);
        toggle = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_drone_condition, container, false);
        thiscontext = container.getContext();


        database.getAllFlights(this::SetupListView);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
/*
        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (toggle == true) {
                    countDowntimer = createTimer();
                    countDowntimer.start();
                    return;
                }
                if (countDowntimer != null) {
                    countDowntimer.onFinish();
                    countDowntimer.cancel();

                }
                //database.add_flight(id, mot_t, rot_t, test, System.currentTimeMillis());

            }
        });*/
    }



        public void SetupListView(List<FlightModel> flights){
            list = view.findViewById(R.id.recycle_view);

            list.setLayoutManager(new LinearLayoutManager(list.getContext()));

            list.setAdapter(new RecyclerViewAdapter(flights));

            list.addOnItemTouchListener(
            new RecyclerItemClickListener(list.getContext(), list, new RecyclerItemClickListener.OnItemClickListener(){
                @Override
                public void onItemClick(View view, int position){
                    RecyclerViewAdapter adapter = (RecyclerViewAdapter) list.getAdapter();

                    database.get_flight(adapter.getFlight(position).getFlightId(), flight ->{
                        SetupGraphView(flight);
                    } );
                }

                @Override
                public void onItemLongClick(View view, int position){}
            })

           );
        }

    private void SetupGraphView(FlightModel flight) {
        double x = 0.0;
        //   GraphView graph = (GraphView) findViewById(R.id.)
    }

    public static Long datetoTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}