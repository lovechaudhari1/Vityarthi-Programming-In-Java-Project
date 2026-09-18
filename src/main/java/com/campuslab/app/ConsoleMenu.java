package com.campuslab.app;

import com.campuslab.enums.BookingStatus;
import com.campuslab.enums.ResourceStatus;
import com.campuslab.enums.ResourceType;
import com.campuslab.exception.CampusLabException;
import com.campuslab.model.Booking;
import com.campuslab.model.Experiment;
import com.campuslab.model.LabResource;
import com.campuslab.model.Student;
import com.campuslab.repository.LabRepository;
import com.campuslab.service.BookingService;
import com.campuslab.service.ResourceService;
import com.campuslab.util.FileManager;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class ConsoleMenu {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final Scanner scanner;
    private final LabRepository repository;
    private final BookingService bookingService;
    private final ResourceService resourceService;
    private final Stack<String> menuHistory = new Stack<>();

    public ConsoleMenu(Scanner scanner, LabRepository repository) {
        this.scanner = scanner;
        this.repository = repository;
        this.bookingService = new BookingService(repository);
        this.resourceService = new ResourceService(repository);
    }

    public void start() {
        while (true) {
            printMainMenu();
            int choice = readInt("Choose option: ", 1, 3);

            try {
                switch (choice) {
                    case 1 -> studentMenu();
                    case 2 -> staffMenu();
                    case 3 -> {
                        System.out.println("\nThank you for using CampusLab.");
                        return;
                    }
                    default -> System.out.println("Invalid option.");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            } catch (CampusLabException e) {
                System.out.println("Operation failed: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("File error: " + e.getMessage());
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n==============================================");
        System.out.println("     CAMPUSLAB - LAB RESOURCE MANAGER");
        System.out.println("==============================================");
        System.out.println("1. Student Menu");
        System.out.println("2. Lab Staff Menu");
        System.out.println("3. Exit");
    }

    private void studentMenu() throws SQLException {
        menuHistory.push("Student Menu");

        String studentId = readNonEmpty("Enter Student ID: ");
        Student student = repository.findStudent(studentId);

        if (student == null) {
            System.out.println("Student not found. Try ST101, ST102 or ST103.");
            menuHistory.pop();
            return;
        }

        while (true) {
            System.out.println("\n--- STUDENT MENU: " + student.getName() + " ---");
            System.out.println("1. View Experiments");
            System.out.println("2. View All Resources");
            System.out.println("3. View Available Resources");
            System.out.println("4. Request Experiment Booking");
            System.out.println("5. View My Bookings");
            System.out.println("6. Cancel My Booking");
            System.out.println("7. Back");

            int choice = readInt("Choose option: ", 1, 7);

            try {
                switch (choice) {
                    case 1 -> printExperiments(repository.findAllExperiments());
                    case 2 -> printResources(resourceService.getAllResources());
                    case 3 -> printResources(resourceService.getAvailableResources());
                    case 4 -> createBooking(student.getUserId());
                    case 5 -> printBookings(bookingService.getStudentBookings(student.getUserId()));
                    case 6 -> cancelBooking(student.getUserId());
                    case 7 -> {
                        menuHistory.pop();
                        return;
                    }
                    default -> System.out.println("Invalid option.");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    private void createBooking(String studentId) throws SQLException {
        String experimentId = readNonEmpty("Enter Experiment ID (EXP201/EXP202/EXP203): ");
        LocalDate date = readDate("Enter booking date (DD-MM-YYYY): ");
        int slot = readInt("Enter slot (1-6): ", 1, 6);

        try {
            Booking booking = bookingService.createBooking(studentId, experimentId, date, slot);
            System.out.println("\nBooking request created successfully.");
            System.out.println("Booking ID : " + booking.getBookingId());
            System.out.println("Status     : " + booking.getStatus());
            System.out.println("Slot       : " + booking.getSlotLabel());
            System.out.println("\nThe request is waiting for lab staff approval.");
        } catch (CampusLabException e) {
            System.out.println("Booking failed: " + e.getMessage());
        }
    }

    private void cancelBooking(String studentId) throws SQLException {
        String bookingId = readNonEmpty("Enter Booking ID: ");
        try {
            bookingService.cancelBooking(studentId, bookingId);
            System.out.println("Booking cancelled successfully.");
        } catch (CampusLabException e) {
            System.out.println("Cancellation failed: " + e.getMessage());
        }
    }

    private void staffMenu() throws SQLException, IOException, CampusLabException {
        menuHistory.push("Lab Staff Menu");

        while (true) {
            System.out.println("\n--- LAB STAFF MENU ---");
            System.out.println("1. View Resources");
            System.out.println("2. Add Resource");
            System.out.println("3. Update Resource Status");
            System.out.println("4. View Students");
            System.out.println("5. View Experiments");
            System.out.println("6. View All Bookings");
            System.out.println("7. Approve Booking");
            System.out.println("8. Reject Booking");
            System.out.println("9. Export Booking Report");
            System.out.println("10. Run Background Backup");
            System.out.println("11. Show Menu History");
            System.out.println("12. Back");

            int choice = readInt("Choose option: ", 1, 12);

            switch (choice) {
                case 1 -> printResourcesSorted(resourceService.getAllResources());
                case 2 -> addResource();
                case 3 -> updateResourceStatus();
                case 4 -> repository.findAllStudents().forEach(System.out::println);
                case 5 -> printExperiments(repository.findAllExperiments());
                case 6 -> printBookings(repository.findAllBookings());
                case 7 -> changeBookingStatus(true);
                case 8 -> changeBookingStatus(false);
                case 9 -> exportReport();
                case 10 -> runBackup();
                case 11 -> showMenuHistory();
                case 12 -> {
                    menuHistory.pop();
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void addResource() throws SQLException, CampusLabException {
        System.out.println("\n--- ADD RESOURCE ---");
        String id = readNonEmpty("Resource ID: ").toUpperCase();
        String name = readNonEmpty("Resource name: ");
        ResourceType type = readResourceType();
        String location = readNonEmpty("Location: ");
        int quantity = readInt("Quantity: ", 1, 1000);

        resourceService.addResource(id, name, type, location, quantity);
        System.out.println("Resource added successfully.");
    }

    private void updateResourceStatus() throws SQLException, CampusLabException {
        String resourceId = readNonEmpty("Resource ID: ").toUpperCase();
        ResourceStatus status = readResourceStatus();
        resourceService.updateStatus(resourceId, status);
        System.out.println("Resource status updated successfully.");
    }

    private void changeBookingStatus(boolean approve) throws SQLException {
        String bookingId = readNonEmpty("Booking ID: ").toUpperCase();
        try {
            if (approve) {
                bookingService.approveBooking(bookingId);
                System.out.println("Booking approved.");
            } else {
                bookingService.rejectBooking(bookingId);
                System.out.println("Booking rejected.");
            }
        } catch (CampusLabException e) {
            System.out.println("Booking update failed: " + e.getMessage());
        }
    }

    private void exportReport() throws SQLException, IOException {
        FileManager.exportBookings(repository.findAllBookings());
        System.out.println("Booking report saved to data/exports/booking-report.csv");
    }

    private void runBackup() throws SQLException {
        List<LabResource> resources = resourceService.getAllResources();
        Thread backupThread = new Thread(new FileManager.BackupTask(resources), "CampusLab-Backup-Thread");
        backupThread.start();
        System.out.println("Background backup started on thread: " + backupThread.getName());
    }

    private void showMenuHistory() {
        if (menuHistory.isEmpty()) {
            System.out.println("No menu history is available.");
            return;
        }

        System.out.println("Menu history:");
        for (int i = 0; i < menuHistory.size(); i++) {
            System.out.println((i + 1) + ". " + menuHistory.get(i));
        }
    }

    private ResourceType readResourceType() {
        System.out.println("1. EQUIPMENT");
        System.out.println("2. EXPERIMENT_KIT");
        System.out.println("3. COMPUTER");

        int choice = readInt("Choose resource type: ", 1, 3);
        return switch (choice) {
            case 1 -> ResourceType.EQUIPMENT;
            case 2 -> ResourceType.EXPERIMENT_KIT;
            default -> ResourceType.COMPUTER;
        };
    }

    private ResourceStatus readResourceStatus() {
        System.out.println("1. AVAILABLE");
        System.out.println("2. UNDER_MAINTENANCE");
        System.out.println("3. UNAVAILABLE");

        int choice = readInt("Choose status: ", 1, 3);
        return switch (choice) {
            case 1 -> ResourceStatus.AVAILABLE;
            case 2 -> ResourceStatus.UNDER_MAINTENANCE;
            default -> ResourceStatus.UNAVAILABLE;
        };
    }

    private void printResources(List<LabResource> resources) {
        System.out.println("\nID       NAME                     DESCRIPTION                QTY   STATUS             LOCATION");
        System.out.println("------------------------------------------------------------------------------------------------");
        if (resources.isEmpty()) {
            System.out.println("No resources found.");
            return;
        }
        resources.forEach(System.out::println);
    }

    private void printResourcesSorted(List<LabResource> resources) {
        List<LabResource> sorted = new ArrayList<>(resources);
        sorted.sort(new Comparator<LabResource>() {
            @Override
            public int compare(LabResource first, LabResource second) {
                return first.getName().compareToIgnoreCase(second.getName());
            }
        });
        printResources(sorted);
    }

    private void printExperiments(List<Experiment> experiments) {
        System.out.println("\nEXPERIMENTS");
        System.out.println("--------------------------------------------------------------------------");
        if (experiments.isEmpty()) {
            System.out.println("No experiments found.");
            return;
        }
        experiments.forEach(experiment -> {
            System.out.println(experiment);
            System.out.println("  Required resources: " + experiment.getRequiredResourceIds());
        });
    }

    private void printBookings(List<Booking> bookings) {
        System.out.println("\nBOOKINGS");
        System.out.println("--------------------------------------------------------------------------");
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        bookings.forEach(System.out::println);
    }

    private String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Input cannot be empty.");
        }
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Ask again with a friendly message.
            }
            System.out.println("Enter a number between " + min + " and " + max + ".");
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return LocalDate.parse(scanner.nextLine().trim(), DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("Use date format DD-MM-YYYY.");
            }
        }
    }
}
