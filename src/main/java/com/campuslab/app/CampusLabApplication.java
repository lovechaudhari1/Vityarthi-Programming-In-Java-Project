package com.campuslab.app;

import com.campuslab.db.DatabaseManager;
import com.campuslab.repository.LabRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Scanner;

public class CampusLabApplication {
    public static void main(String[] args) {
        System.out.println("\nStarting CampusLab...");

        try {
            Files.createDirectories(Path.of("data", "exports"));
            Files.createDirectories(Path.of("data", "backups"));

            DatabaseManager.getInstance().initializeDatabase();

            LabRepository repository = new LabRepository();
            repository.seedDemoData();

            System.out.println("Database ready.");
            System.out.println("Demo student IDs: ST101, ST102, ST103");
            System.out.println("Demo experiment IDs: EXP201, EXP202, EXP203");

            try (Scanner scanner = new Scanner(System.in)) {
                new ConsoleMenu(scanner, repository).start();
            }
        } catch (SQLException | java.io.IOException | IllegalStateException e) {
            System.out.println("CampusLab could not start.");
            System.out.println("Reason: " + e.getMessage());
            System.out.println("Run the application from the project root using:");
            System.out.println("mvn clean compile exec:java");
        }
    }
}
