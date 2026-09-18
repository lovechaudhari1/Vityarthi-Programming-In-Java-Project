package com.campuslab.service;

import com.campuslab.enums.BookingStatus;
import com.campuslab.exception.CampusLabException;
import com.campuslab.model.Booking;
import com.campuslab.model.Experiment;
import com.campuslab.repository.LabRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class BookingService {
    private final LabRepository repository;

    public BookingService(LabRepository repository) {
        this.repository = repository;
    }

    public Booking createBooking(String studentId, String experimentId, LocalDate date, int slot)
            throws SQLException, CampusLabException {
        if (repository.findStudent(studentId) == null) {
            throw new CampusLabException("Student ID was not found.");
        }

        if (date.isBefore(LocalDate.now())) {
            throw new CampusLabException("Booking date cannot be in the past.");
        }

        if (slot < 1 || slot > 6) {
            throw new CampusLabException("Slot must be between 1 and 6.");
        }

        Experiment experiment = repository.findExperiment(experimentId);
        if (experiment == null) {
            throw new CampusLabException("Experiment ID was not found.");
        }

        if (repository.hasStudentBooking(studentId, date.toString(), slot)) {
            throw new CampusLabException("The student already has a booking for this date and slot.");
        }

        int currentBookings = repository.countActiveBookings(experimentId, date.toString(), slot);
        if (currentBookings >= experiment.getMaxStudents()) {
            throw new CampusLabException("This experiment has reached its slot capacity.");
        }

        if (!repository.hasAvailableRequiredResource(experimentId)) {
            throw new CampusLabException("A required laboratory resource is currently unavailable.");
        }

        String bookingId = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return saveNewBooking(bookingId, studentId, experimentId, date, slot);
    }

    public Booking createBooking(String studentId, String experimentId, LocalDate date, int slot, boolean priority)
            throws SQLException, CampusLabException {
        // Overloaded method: priority is currently informational for future policy rules.
        return createBooking(studentId, experimentId, date, slot);
    }

    private Booking saveNewBooking(String bookingId, String studentId, String experimentId,
                                    LocalDate date, int slot) throws SQLException {
        Booking booking = new Booking(bookingId, studentId, experimentId, date, slot,
                BookingStatus.PENDING, LocalDateTime.now());
        repository.saveBooking(booking);
        return booking;
    }

    public List<Booking> getStudentBookings(String studentId) throws SQLException {
        return repository.findBookingsByStudent(studentId);
    }

    public void cancelBooking(String studentId, String bookingId) throws SQLException, CampusLabException {
        Booking booking = repository.findBooking(bookingId);
        if (booking == null) {
            throw new CampusLabException("Booking ID was not found.");
        }
        if (!booking.getStudentId().equalsIgnoreCase(studentId)) {
            throw new CampusLabException("You can only cancel your own booking.");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new CampusLabException("Booking is already cancelled.");
        }
        if (booking.getStatus() == BookingStatus.REJECTED) {
            throw new CampusLabException("A rejected booking cannot be cancelled.");
        }
        repository.updateBookingStatus(bookingId, BookingStatus.CANCELLED);
    }

    public void approveBooking(String bookingId) throws SQLException, CampusLabException {
        Booking booking = requireBooking(bookingId);
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new CampusLabException("Only pending bookings can be approved.");
        }
        repository.updateBookingStatus(bookingId, BookingStatus.CONFIRMED);
    }

    public void rejectBooking(String bookingId) throws SQLException, CampusLabException {
        Booking booking = requireBooking(bookingId);
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new CampusLabException("Only pending bookings can be rejected.");
        }
        repository.updateBookingStatus(bookingId, BookingStatus.REJECTED);
    }

    private Booking requireBooking(String bookingId) throws SQLException, CampusLabException {
        Booking booking = repository.findBooking(bookingId);
        if (booking == null) {
            throw new CampusLabException("Booking ID was not found.");
        }
        return booking;
    }
}
