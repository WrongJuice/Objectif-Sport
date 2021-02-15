package com.example.objectifsport.model.goals;

import java.util.Calendar;

public class DistanceGoal extends Goal {

    private double distance, completedDistance;

    public DistanceGoal(String type, Calendar startDate, Calendar endDate, double distance) {
        super(type, startDate, endDate);
        this.distance = distance;
    }
}