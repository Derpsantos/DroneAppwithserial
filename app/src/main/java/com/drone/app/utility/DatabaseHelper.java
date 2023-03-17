package com.drone.app.utility;

import androidx.annotation.NonNull;

import com.drone.app.adapters.ComponentHandler;
import com.drone.app.adapters.FlightHandler;
import com.drone.app.adapters.FlightListHandler;
import com.drone.app.adapters.FlightRHandler;
import com.drone.app.adapters.FlightRListHandler;
import com.drone.app.models.ComponentUsage;
import com.drone.app.models.FlightModel;
import com.drone.app.models.FlightRecordings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    private static final String KEY_FLIGHT = "flight";
    private static final String KEY_FLIGHT_R = "FlightR";
    private static final String COMP_KEY="Components";

    FirebaseDatabase database;

    public DatabaseHelper() {
        this.database = FirebaseDatabase.getInstance();
    }

    public void add_flight(String id, double motor1, double motor2, double motor3, double motor4, double humidity, double ambient, double altitude, double latitude, double longitude, long timestamp) {
        FlightModel Flight = new FlightModel(id, motor1, motor2, motor3, motor4, humidity, ambient, altitude, latitude, longitude, timestamp);

        database.getReference().child(KEY_FLIGHT).child(id).setValue(Flight);
    }

    public void initializecomponents(long m1, long m2, long m3, long m4, long b){
        ComponentUsage comp = new ComponentUsage(m1,m2,m3,m4,b);
        database.getReference().child(COMP_KEY).setValue(comp);

    }
    public void getcomponentusage(ComponentHandler handler){

        database.getReference().child(COMP_KEY).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ComponentUsage comp = snapshot.getValue(ComponentUsage.class);
                handler.handle(comp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    public void add_flight_recordings(String id, List<Double> motor_1, List<Double> motor2, List<Double> motor3, List<Double> motor4, List<Double> humid, List<Double> ambients, List<Double>altitudes, long timestamp) {
        FlightRecordings flight = new FlightRecordings(id, motor_1, motor2, motor3, motor4, humid, ambients, altitudes, timestamp);
        database.getReference().child(KEY_FLIGHT_R).child(id).setValue(flight);
    }

    public void get_flight(String id, FlightHandler handler) {
        if(database.getReference().child(KEY_FLIGHT)!=null) {
            database.getReference().child(KEY_FLIGHT).child(id).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    FlightModel flight = snapshot.getValue(FlightModel.class);
                    handler.handle(flight);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void get_flightRecordings(String id, FlightRHandler handler){
        if(database.getReference().child(KEY_FLIGHT_R) !=null) {
            database.getReference().child(KEY_FLIGHT_R).child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    FlightRecordings flight = snapshot.getValue(FlightRecordings.class);
                    handler.handle(flight);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void getAllFlights(FlightListHandler handler) {
        database.getReference().child(KEY_FLIGHT).orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<FlightModel> flights = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    flights.add(child.getValue(FlightModel.class));
                }
                handler.handle(flights);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getAllFlightsR(FlightRListHandler handler) {
        database.getReference().child(KEY_FLIGHT).orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<FlightRecordings> flights = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    flights.add(child.getValue(FlightRecordings.class));
                }
                handler.handle(flights);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
