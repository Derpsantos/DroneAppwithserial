package com.drone.app.adapters;

import com.drone.app.models.FlightModel;
import com.drone.app.models.FlightRecordings;

import java.util.List;

public interface FlightRListHandler {

    void handle(List<FlightRecordings> flights);
}
