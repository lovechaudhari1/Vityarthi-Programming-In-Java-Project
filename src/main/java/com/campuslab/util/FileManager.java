package com.campuslab.util;

import com.campuslab.model.Booking;
import com.campuslab.model.LabResource;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public final class FileManager {
    private FileManager() {
    }

    public static synchronized void exportBookings(List<Booking> bookings) throws IOException {
        Path target = Path.of("data", "exports", "booking-report.csv");
        Files.createDirectories(target.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(target, StandardCharsets.UTF_8)) {
            writer.write("booking_id,student_id,experiment_id,date,slot,status,created_at");
            writer.newLine();
            for (Booking booking : bookings) {
                writer.write(String.join(",",
                        booking.getBookingId(),
                        booking.getStudentId(),
                        booking.getExperimentId(),
                        booking.getBookingDate().toString(),
                        String.valueOf(booking.getSlot()),
                        booking.getStatus().name(),
                        booking.getCreatedAt().toString()));
                writer.newLine();
            }
        }
    }

    public static synchronized void createResourceBackup(List<LabResource> resources) throws IOException {
        Path source = Path.of("data", "backups", "resource-backup.txt");
        Files.createDirectories(source.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(source, StandardCharsets.UTF_8)) {
            writer.write("CampusLab Resource Backup - " + LocalDateTime.now());
            writer.newLine();
            writer.write("========================================================");
            writer.newLine();
            for (LabResource resource : resources) {
                writer.write(resource.toString());
                writer.newLine();
            }
        }

        Path binaryCopy = Path.of("data", "backups", "resource-backup-copy.bin");
        try (InputStream input = new BufferedInputStream(Files.newInputStream(source));
             OutputStream output = new BufferedOutputStream(Files.newOutputStream(binaryCopy))) {
            byte[] buffer = new byte[1024];
            int count;
            while ((count = input.read(buffer)) != -1) {
                output.write(buffer, 0, count);
            }
        }
    }

    public static class BackupTask implements Runnable {
        private final List<LabResource> resources;

        public BackupTask(List<LabResource> resources) {
            this.resources = resources;
        }

        @Override
        public void run() {
            try {
                createResourceBackup(resources);
                System.out.println("Background backup completed.");
            } catch (IOException e) {
                System.out.println("Backup failed: " + e.getMessage());
            }
        }
    }
}
