package com.drone.app.adapters;

import com.drone.app.models.ComponentUsage;

import java.util.List;

public interface ComponentListHandler {

    void handle(List<ComponentUsage> flights);
}
