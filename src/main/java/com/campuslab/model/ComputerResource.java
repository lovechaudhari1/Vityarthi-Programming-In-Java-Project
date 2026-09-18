package com.campuslab.model;

public class ComputerResource extends LabResource {
    private final String operatingSystem;

    public ComputerResource(String resourceId, String name, String location, int quantity, String operatingSystem) {
        super(resourceId, name, location, quantity);
        this.operatingSystem = operatingSystem;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    @Override
    public String getDescription() {
        return "Computer - " + operatingSystem;
    }
}
