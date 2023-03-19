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
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.drone.app.MainActivity;
import com.drone.app.R;
import com.drone.app.adapters.FlightHandler;
import com.drone.app.adapters.FlightListHandler;
import com.drone.app.models.FlightModel;
import com.drone.app.models.FlightRecordings;
import com.drone.app.utility.DatabaseHelper;
import com.drone.app.utility.RecyclerItemClickListener;
import com.drone.app.utility.RecyclerViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class DroneConditionFragment extends Fragment {
    View view;

    List<FlightModel> flights;
    List<FlightRecordings> flightsR;
    DatabaseHelper database;
    RecyclerView list;
    private FirebaseAuth auth;
    private FirebaseUser user;
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
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_drone_condition, container, false);
        thiscontext = container.getContext();


        database.getAllFlights(this::SetupGraphView);
        //database.getAllFlightsR(this::SetupListView);

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


        /*
        public void SetupListView(List<FlightRecordings> flights){
            list = view.findViewById(R.id.recycle_view);

            list.setLayoutManager(new LinearLayoutManager(list.getContext()));

            list.setAdapter(new RecyclerViewAdapter(flights));

            list.addOnItemTouchListener(
            new RecyclerItemClickListener(list.getContext(), list, new RecyclerItemClickListener.OnItemClickListener(){
                @Override
                public void onItemClick(View view, int position){

                    RecyclerViewAdapter adapter = (RecyclerViewAdapter) list.getAdapter();
                    Log.e("test", "" + adapter.getFlight(position).getId() + "");
                    /*database.get_flightRecordings(adapter.getFlight(position).getId(), flight ->{
                        SetupGraphView(flight);
                    } );
                }

                @Override
                public void onItemLongClick(View view, int position){}
            })

           );
        }
*/


    private void SetupGraphView(List<FlightModel> flight) {
        int x = 0;

        GraphView graph = (GraphView) view.findViewById(R.id.Drone_graph);
        graph.removeAllSeries();

        LineGraphSeries<DataPoint> Motor1series = new LineGraphSeries<>();
        Motor1series.setColor(Color.RED);
        Motor1series.setTitle("Motor 1 temperatures");
        if(flight.size()<100) {
            for (int i = 10; i > 0; i--) {
                Motor1series.appendData(new DataPoint(x, flight.get(flight.size() - i).getMotor1_temp_max()), true, 30);
                x += 1;
            }
            graph.addSeries(Motor1series);
        }
        else{
            for(int i = 30; i>0; i--){
                Motor1series.appendData(new DataPoint(x, flight.get(flight.size()-i).getMotor1_temp_max()),true,30);
                x+=1;
            }

        }

        //******************************************************************************

        LineGraphSeries<DataPoint> Motor2series = new LineGraphSeries<>();
        Motor2series.setColor(Color.GREEN);
        Motor2series.setTitle("Motor 2 temperatures");
        x=0;
        if(flight.size()<100) {
            for (int i = 10; i > 0; i--) {
                Motor2series.appendData(new DataPoint(x, flight.get(flight.size() - i).getMotor2_temp_max()), true, 30);
                x += 1;
            }
            graph.addSeries(Motor2series);
        }
        else{
            for(int i = 30; i>0; i--){
                Motor2series.appendData(new DataPoint(x, flight.get(flight.size()-i).getMotor2_temp_max()),true,30);
                x+=1;
            }
        }

        //*************************************************************************************
        x=0;
        LineGraphSeries<DataPoint> Motor3series = new LineGraphSeries<>();
        Motor3series.setColor(Color.BLUE);
        Motor3series.setTitle("Motor 3 temperatures");
        if(flight.size()<100) {
            for (int i = 10; i > 0; i--) {
                Motor3series.appendData(new DataPoint(x, flight.get(flight.size() - i).getMotor3_temp_max()), true, 30);
                x += 1;
            }
            graph.addSeries(Motor3series);
        }
        else{
            for(int i = 30; i>0; i--){
                Motor3series.appendData(new DataPoint(x, flight.get(flight.size()-i).getMotor3_temp_max()),true,30);
                x+=1;
            }
        }

        //***********************************************************************************
    x=0;
        LineGraphSeries<DataPoint> Motor4series = new LineGraphSeries<>();
        Motor4series.setColor(Color.YELLOW);
        Motor4series.setTitle("Motor 4 temperatures");
        if(flight.size()<100) {
            for (int i = 10; i > 0; i--) {
                Motor4series.appendData(new DataPoint(x, flight.get(flight.size() - i).getMotor4_temp_max()), true, 30);
                x += 1;
            }
            graph.addSeries(Motor4series);
        }
        else{
            for(int i = 30; i>0; i--){
                Motor4series.appendData(new DataPoint(x, flight.get(flight.size()-i).getMotor4_temp_max()),true,30);
                x+=1;
            }
        }

        //********************************************************************************************
    x=0;
        LineGraphSeries<DataPoint> Batteryseries = new LineGraphSeries<>();
        Batteryseries.setColor(Color.BLACK);
        Batteryseries.setTitle("Battery temperatures");
        if(flight.size()<100) {
            for (int i = 10; i > 0; i--) {
                Batteryseries.appendData(new DataPoint(x, flight.get(flight.size() - i).getBattery_max_max()), true, 30);
                x += 1;
            }
            graph.addSeries(Batteryseries);
        }
        else{
            for(int i = 30; i>0; i--){
                Batteryseries.appendData(new DataPoint(x, flight.get(flight.size()-i).getBattery_max_max()),true,30);
                x+=1;
            }
        }

        graph.setTitle(" Recent Flights Max Temperatures");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        GridLabelRenderer gridLabel=graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Flight number (Oldest to Most Recent, Left to Right)");
        //gridLabel.setVerticalAxisTitle("Maximum temperature");


        //Log.e("test", "" + flight.get(0).getFlightId()+ "");
       // Log.e("test", "" + flight.get(0).getMotor1_temp_max()+ "");
        //   GraphView graph = (GraphView) findViewById(R.id.)
    }

    public static Long datetoTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}