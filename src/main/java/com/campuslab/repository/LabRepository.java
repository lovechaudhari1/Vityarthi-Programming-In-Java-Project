package com.campuslab.repository;

import com.campuslab.db.DatabaseManager;
import com.campuslab.enums.BookingStatus;
import com.campuslab.enums.ResourceStatus;
import com.campuslab.enums.ResourceType;
import com.campuslab.model.Booking;
import com.campuslab.model.ComputerResource;
import com.campuslab.model.Equipment;
import com.campuslab.model.Experiment;
import com.campuslab.model.LabResource;
import com.campuslab.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LabRepository {
    private final DatabaseManager databaseManager = DatabaseManager.getInstance();

    public void seedDemoData() throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {
            insertStudent(connection, "ST101", "Aarav Sharma", "aarav@example.com", "CSE", 2);
            insertStudent(connection, "ST102", "Meera Patil", "meera@example.com", "CSE", 2);
            insertStudent(connection, "ST103", "Riya Verma", "riya@example.com", "AIML", 2);

            insertResource(connection, "RES101", "Arduino Starter Kit", ResourceType.EXPERIMENT_KIT, "IoT Lab", 8, ResourceStatus.AVAILABLE);
            insertResource(connection, "RES102", "Digital Multimeter", ResourceType.EQUIPMENT, "Electronics Lab", 4, ResourceStatus.AVAILABLE);
            insertResource(connection, "RES103", "Lab PC", ResourceType.COMPUTER, "Programming Lab", 20, ResourceStatus.AVAILABLE);
            insertResource(connection, "RES104", "Raspberry Pi Kit", ResourceType.EXPERIMENT_KIT, "AIML Lab", 5, ResourceStatus.AVAILABLE);

            insertExperiment(connection, "EXP201", "IoT Temperature Monitoring", "IoT", 60, 4);
            insertExperiment(connection, "EXP202", "Digital Circuit Analysis", "Electronics", 60, 4);
            insertExperiment(connection, "EXP203", "Raspberry Pi Sensor Logging", "AIML", 90, 5);

            linkExperimentResource(connection, "EXP201", "RES101", 1);
            linkExperimentResource(connection, "EXP202", "RES102", 1);
            linkExperimentResource(connection, "EXP203", "RES104", 1);
        }
    }

    private void insertStudent(Connection c, String id, String name, String email,
                               String department, int semester) throws SQLException {
        String sql = "INSERT OR IGNORE INTO students VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, id);
            p.setString(2, name);
            p.setString(3, email);
            p.setString(4, department);
            p.setInt(5, semester);
            p.executeUpdate();
        }
    }

    private void insertResource(Connection c, String id, String name, ResourceType type,
                                String location, int quantity, ResourceStatus status) throws SQLException {
        String sql = "INSERT OR IGNORE INTO resources VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, id);
            p.setString(2, name);
            p.setString(3, type.name());
            p.setString(4, location);
            p.setInt(5, quantity);
            p.setString(6, status.name());
            p.executeUpdate();
        }
    }

    private void insertExperiment(Connection c, String id, String name, String type,
                                   int duration, int maxStudents) throws SQLException {
        String sql = "INSERT OR IGNORE INTO experiments VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, id);
            p.setString(2, name);
            p.setString(3, type);
            p.setInt(4, duration);
            p.setInt(5, maxStudents);
            p.executeUpdate();
        }
    }

    private void linkExperimentResource(Connection c, String experimentId, String resourceId,
                                        int requiredQuantity) throws SQLException {
        String sql = "INSERT OR IGNORE INTO experiment_resources VALUES (?, ?, ?)";
        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, experimentId);
            p.setString(2, resourceId);
            p.setInt(3, requiredQuantity);
            p.executeUpdate();
        }
    }

    public Student findStudent(String id) throws SQLException {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (Connection c = databaseManager.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, id);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return new Student(rs.getString("student_id"), rs.getString("name"),
                            rs.getString("email"), rs.getString("department"), rs.getInt("semester"));
                }
            }
        }
        return null;
    }

    public List<Student> findAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY student_id";
        try (Connection c = databaseManager.getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                students.add(new Student(rs.getString("student_id"), rs.getString("name"),
                        rs.getString("email"), rs.getString("department"), rs.getInt("semester")));
            }
        }
        return students;
    }

    public List<LabResource> findAllResources() throws SQLException {
        List<LabResource> resources = new ArrayList<>();
        String sql = "SELECT * FROM resources ORDER BY resource_id";

        try (Connection c = databaseManager.getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                resources.add(mapResource(rs));
            }
        }
        return resources;
    }

    private LabResource mapResource(ResultSet rs) throws SQLException {
        String type = rs.getString("resource_type");
        LabResource resource;

        if (ResourceType.COMPUTER.name().equals(type)) {
            resource = new ComputerResource(
                    rs.getString("resource_id"), rs.getString("name"), rs.getString("location"),
                    rs.getInt("quantity"), "Windows/Linux");
        } else {
            resource = new Equipment(
                    rs.getString("resource_id"), rs.getString("name"), rs.getString("location"),
                    rs.getInt("quantity"), type.equals(ResourceType.EXPERIMENT_KIT.name()) ? "Experiment Kit" : "Lab Equipment");
        }

        resource.setStatus(ResourceStatus.valueOf(rs.getString("status")));
        return resource;
    }

    public boolean resourceExists(String resourceId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM resources WHERE resource_id = ?";
        try (Connection c = databaseManager.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, resourceId);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public void addResource(LabResource resource, ResourceType type) throws SQLException {
        String sql = "INSERT INTO resources VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = databaseManager.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, resource.getResourceId());
            p.setString(2, resource.getName());
            p.setString(3, type.name());
            p.setString(4, resource.getLocation());
            p.setInt(5, resource.getQuantity());
            p.setString(6, resource.getStatus().name());
            p.executeUpdate();
        }
    }

    public void updateResourceStatus(String resourceId, ResourceStatus status) throws SQLException {
        String sql = "UPDATE resources SET status = ? WHERE resource_id = ?";
        try (Connection c = databaseManager.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, status.name());
            p.setString(2, resourceId);
            if (p.executeUpdate() == 0) {
                throw new SQLException("Resource ID was not found.");
            }
        }
    }

    public List<Experiment> findAllExperiments() throws SQLException {
        List<Experiment> experiments = new ArrayList<>();
        String sql = "SELECT * FROM experiments ORDER BY experiment_id";
        try (Connection c = databaseManager.getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                Experiment experiment = new Experiment(rs.getString("experiment_id"), rs.getString("name"),
                        rs.getString("experiment_type"), rs.getInt("duration_minutes"), rs.getInt("max_students"));
                loadRequiredResources(c, experiment);
                experiments.add(experiment);
            }
        }
        return experiments;
    }

    private void loadRequiredResources(Connection c, Experiment experiment) throws SQLException {
        String sql = "SELECT resource_id FROM experiment_resources WHERE experiment_id = ?";
        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, experiment.getExperimentId());
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    experiment.addRequiredResource(rs.getString("resource_id"));
                }
            }
        }
    }

    public Experiment findExperiment(String id) throws SQLException {
        for (Experiment experiment : findAllExperiments()) {
            if (experiment.getExperimentId().equalsIgnoreCase(id)) {
                return experiment;
            }
        }
        return null;
    }

    public boolean hasAvailableRequiredResource(String experimentId) throws SQLException {
        String sql = "SELECT r.status, r.quantity, er.required_quantity "
                + "FROM experiment_resources er JOIN resources r ON r.resource_id = er.resource_id "
                + "WHERE er.experiment_id = ?";

        try (Connection c = databaseManager.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, experimentId);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    if (!ResourceStatus.AVAILABLE.name().equals(rs.getString("status"))
                            || rs.getInt("quantity") < rs.getInt("required_quantity")) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean hasStudentBooking(String studentId, String date, int slot) throws SQLException {
        String sql = "SELECT COUNT(*) FROM bookings WHERE student_id = ? AND booking_date = ? "
                + "AND slot = ? AND status IN ('PENDING', 'CONFIRMED')";
        try (Connection c = databaseManager.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, studentId);
            p.setString(2, date);
            p.setInt(3, slot);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public int countActiveBookings(String experimentId, String date, int slot) throws SQLException {
        String sql = "SELECT COUNT(*) FROM bookings WHERE experiment_id = ? AND booking_date = ? "
                + "AND slot = ? AND status IN ('PENDING', 'CONFIRMED')";
        try (Connection c = databaseManager.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, experimentId);
            p.setString(2, date);
            p.setInt(3, slot);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public void saveBooking(Booking booking) throws SQLException {
        String sql = "INSERT INTO bookings VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = databaseManager.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, booking.getBookingId());
            p.setString(2, booking.getStudentId());
            p.setString(3, booking.getExperimentId());
            p.setString(4, booking.getBookingDate().toString());
            p.setInt(5, booking.getSlot());
            p.setString(6, booking.getStatus().name());
            p.setString(7, booking.getCreatedAt().toString());
            p.executeUpdate();
        }
    }

    public List<Booking> findBookingsByStudent(String studentId) throws SQLException {
        return findBookings("SELECT * FROM bookings WHERE student_id = ? ORDER BY booking_date, slot", studentId);
    }

    public List<Booking> findAllBookings() throws SQLException {
        return findBookings("SELECT * FROM bookings ORDER BY booking_date, slot", null);
    }

    private List<Booking> findBookings(String sql, String studentId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        try (Connection c = databaseManager.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            if (studentId != null) {
                p.setString(1, studentId);
            }
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    bookings.add(new Booking(
                            rs.getString("booking_id"), rs.getString("student_id"), rs.getString("experiment_id"),
                            LocalDate.parse(rs.getString("booking_date")), rs.getInt("slot"),
                            BookingStatus.valueOf(rs.getString("status")),
                            LocalDateTime.parse(rs.getString("created_at"))));
                }
            }
        }
        return bookings;
    }

    public Booking findBooking(String bookingId) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        try (Connection c = databaseManager.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, bookingId);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return new Booking(
                            rs.getString("booking_id"), rs.getString("student_id"), rs.getString("experiment_id"),
                            LocalDate.parse(rs.getString("booking_date")), rs.getInt("slot"),
                            BookingStatus.valueOf(rs.getString("status")),
                            LocalDateTime.parse(rs.getString("created_at")));
                }
            }
        }
        return null;
    }

    public void updateBookingStatus(String bookingId, BookingStatus status) throws SQLException {
        String sql = "UPDATE bookings SET status = ? WHERE booking_id = ?";
        try (Connection c = databaseManager.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, status.name());
            p.setString(2, bookingId);
            if (p.executeUpdate() == 0) {
                throw new SQLException("Booking ID was not found.");
            }
        }
    }

    public String getDatabaseUrl() {
        return "jdbc:sqlite:campuslab.db";
    }
}
