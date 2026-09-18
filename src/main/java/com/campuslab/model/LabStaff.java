package com.campuslab.model;

public class LabStaff extends User {
    private final String department;

    public LabStaff(String userId, String name, String email, String department) {
        super(userId, name, email);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public String getRole() {
        return "LAB_STAFF";
    }

    @Override
    public String toString() {
        return getUserId() + " | " + getName() + " | " + department + " | " + getRole();
    }
}
