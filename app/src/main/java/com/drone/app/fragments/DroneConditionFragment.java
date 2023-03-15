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


       // database.getAllFlights(this::SetupListView);

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
    public CountDownTimer createTimer() {
        return new CountDownTimer(10000, 500) {

            Random r = new Random();
            ArrayList<Double> motor_temps1 = new ArrayList<>();
            ArrayList<Double> motor_temps2 = new ArrayList<>();
            ArrayList<Double> motor_temps3 = new ArrayList<>();
            ArrayList<Double> motor_temps4 = new ArrayList<>();
            ArrayList<Double> humid_temps = new ArrayList<>();
            ArrayList<Double> battery_temps = new ArrayList<>();

            //double motor = 10;
            double motor1 = 80 + (80-20) * r.nextDouble();
            double motor2 = 80 + (80-20) * r.nextDouble();
            double motor3 = 80 + (80-20) * r.nextDouble();
            double motor4 = 80 + (80-20) * r.nextDouble();
            double humid = 80 + (80-20) * r.nextDouble();
            double battery = 50 + (50-10) * r.nextDouble();
            boolean rotor=false;
            final String id = UUID.randomUUID().toString();



            @Override
            public void onTick(long l) {
                //motor++;
                motor1 = (80-20) * r.nextDouble();
                motor2 = (80-20) * r.nextDouble();
                motor3 = (80-20) * r.nextDouble();
                motor4 = (80-20) * r.nextDouble();
                humid = (80-20) * r.nextDouble();
                battery = 50 + (50-10) * r.nextDouble();
                t4.setText(String.format("Temperature of motor 1: %2f", motor1) + " C");
                t3.setText(String.format("Temperature of motor 2: %2f" , motor2) +" C");
                t6.setText(String.format("Temperature of motor 3: %2f", motor3) + " C");
                t7.setText(String.format("Temperature of motor 4: %2f", motor4) + " C");
                t8.setText(String.format("Temperature of humidity: %2f", humid) + " C");
                t9.setText(String.format("Temperature of battery: %2f", battery) + " C");
                t10.setText("Rotor issue : " + rotor );
                t11.setText("Time left: " + l/1000 + "s");
                motor_temps1.add(motor1);
                motor_temps2.add(motor2);
                motor_temps3.add(motor3);
                motor_temps4.add(motor4);
                battery_temps.add(battery);
                humid_temps.add(humid);


                if(motor1>50 || motor2>50 ||motor3>50 || motor4>50){
                    int NOTIFICATION_ID = 234;
                    NotificationManager notificationManager = (NotificationManager) thiscontext.getSystemService(Context.NOTIFICATION_SERVICE);
                    String CHANNEL_ID;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        CHANNEL_ID = "my_channel_01";
                        CharSequence name = "my_channel";
                        String Description = "This is my channel";
                        int importance = NotificationManager.IMPORTANCE_HIGH;
                        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                        mChannel.setDescription(Description);
                        mChannel.enableLights(true);
                        mChannel.setLightColor(Color.RED);
                        mChannel.enableVibration(true);
                        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                        mChannel.setShowBadge(false);
                        notificationManager.createNotificationChannel(mChannel);


                        NotificationCompat.Builder builder = new NotificationCompat.Builder(thiscontext, CHANNEL_ID)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Issue")
                                .setContentText("Temperature too high");

                        Intent resultIntent = new Intent(thiscontext, MainActivity.class);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(thiscontext);
                        stackBuilder.addParentStack(MainActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setContentIntent(resultPendingIntent);
                        notificationManager.notify(NOTIFICATION_ID, builder.build());
                    }
                }


            }

            @Override
            public void onFinish() {
                double motor1max=0;
                double motor2max=0;
                double motor3max=0;
                double motor4max=0;
                double humidity_max=0;
                double battery_max=0;

                for(int i = 0; i < motor_temps1.size(); i++){
                    if(motor_temps1.get(i)>motor1max){
                        motor1max= motor_temps1.get(i);
                    }
                }
                for(int i = 0; i < battery_temps.size(); i++){
                    if(battery_temps.get(i)>battery_max){
                        battery_max= battery_temps.get(i);
                    }
                }


                //database.add_flight(id, motor1max,  battery_max, rotor, System.currentTimeMillis());
                //database.add_flight_recordings(id, motor_temps1, motor_temps2, motor_temps3, motor_temps4, battery_temps, humid_temps, System.currentTimeMillis());
                toggle = false;
            }
        };
    }
*/

/*
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
*/
    private void SetupGraphView(FlightModel flight) {
        double x = 0.0;
        //   GraphView graph = (GraphView) findViewById(R.id.)
    }

    public static Long datetoTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}