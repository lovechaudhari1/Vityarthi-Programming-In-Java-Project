package com.campuslab.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Experiment {
    private final String experimentId;
    private String name;
    private String type;
    private int durationMinutes;
    private int maxStudents;
    private final List<String> requiredResourceIds = new ArrayList<>();

    public Experiment(String experimentId, String name, String type, int durationMinutes, int maxStudents) {
        this.experimentId = experimentId;
        this.name = name;
        this.type = type;
        this.durationMinutes = durationMinutes;
        this.maxStudents = maxStudents;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public List<String> getRequiredResourceIds() {
        return Collections.unmodifiableList(requiredResourceIds);
    }

    public void addRequiredResource(String resourceId) {
        if (!requiredResourceIds.contains(resourceId)) {
            requiredResourceIds.add(resourceId);
        }
    }

    @Override
    public String toString() {
        return experimentId + " | " + name + " | " + type + " | "
                + durationMinutes + " min | Capacity " + maxStudents;
    }
}
