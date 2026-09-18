package com.campuslab.model;

public class Student extends User {
    private String department;
    private int semester;

    public Student(String userId, String name, String email, String department, int semester) {
        super(userId, name, email);
        this.department = department;
        this.semester = semester;
    }

    public String getDepartment() {
        return department;
    }

    public int getSemester() {
        return semester;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    @Override
    public String getRole() {
        return "STUDENT";
    }

    @Override
    public String toString() {
        return getUserId() + " | " + getName() + " | " + department + " | Semester " + semester;
    }
}
