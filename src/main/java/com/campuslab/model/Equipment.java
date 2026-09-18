package com.campuslab.model;

public class Equipment extends LabResource {
    private final String category;

    public Equipment(String resourceId, String name, String location, int quantity, String category) {
        super(resourceId, name, location, quantity);
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String getDescription() {
        return "Equipment - " + category;
    }
}
