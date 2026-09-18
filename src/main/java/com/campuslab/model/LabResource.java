package com.campuslab.model;

import com.campuslab.enums.ResourceStatus;
import com.campuslab.interfaces.Reservable;

public abstract class LabResource implements Reservable {
    private final String resourceId;
    private String name;
    private String location;
    private int quantity;
    private ResourceStatus status;

    protected LabResource(String resourceId, String name, String location, int quantity) {
        this.resourceId = resourceId;
        this.name = name;
        this.location = location;
        this.quantity = quantity;
        this.status = ResourceStatus.AVAILABLE;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public int getQuantity() {
        return quantity;
    }

    public ResourceStatus getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setStatus(ResourceStatus status) {
        this.status = status;
    }

    @Override
    public boolean isAvailable() {
        return status == ResourceStatus.AVAILABLE && quantity > 0;
    }

    @Override
    public void reserve() {
        if (isAvailable()) {
            status = ResourceStatus.AVAILABLE;
        }
    }

    public abstract String getDescription();

    public String getTypeName() {
        return getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return String.format("%-8s %-24s %-25s %-5d %-18s %s",
                resourceId, name, getDescription(), quantity, status, location);
    }
}
