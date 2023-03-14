package com.drone.app.utility;

import androidx.annotation.NonNull;

import com.drone.app.adapters.FlightHandler;
import com.drone.app.adapters.FlightListHandler;
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

    FirebaseDatabase database;

    public DatabaseHelper() {
        this.database = FirebaseDatabase.getInstance();
    }

    public void add_flight(String id, double motor1, double motor2, double motor3, double motor4, double humidity, double ambient, double altitude, double latitude, double longitude, long timestamp) {
        FlightModel Flight = new FlightModel(id, motor1, motor2, motor3, motor4, humidity, ambient, altitude, latitude, longitude, timestamp);

        database.getReference().child(KEY_FLIGHT).child(id).setValue(Flight);
    }

    public void add_flight_recordings(String id, List<Double> motor_1, List<Double> motor2, List<Double> motor3, List<Double> motor4, List<Double> humid, List<Double> ambients, List<Double>altitudes, long timestamp) {
        FlightRecordings flight = new FlightRecordings(id, motor_1, motor2, motor3, motor4, humid, ambients, altitudes, timestamp);
        database.getReference().child(KEY_FLIGHT_R).child(id).setValue(flight);
    }

    public void get_flight(String id, FlightHandler handler) {
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
}
