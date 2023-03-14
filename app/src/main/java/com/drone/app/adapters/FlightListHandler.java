package com.drone.app.adapters;

import com.drone.app.models.FlightModel;

import java.util.List;

public interface FlightListHandler {
    void handle(List<FlightModel> flights);
}
