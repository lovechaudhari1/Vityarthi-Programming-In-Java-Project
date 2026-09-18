package com.campuslab.model;

import com.campuslab.enums.BookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Booking {
    private final String bookingId;
    private final String studentId;
    private final String experimentId;
    private final LocalDate bookingDate;
    private final int slot;
    private BookingStatus status;
    private final LocalDateTime createdAt;

    public Booking(String bookingId, String studentId, String experimentId,
                   LocalDate bookingDate, int slot, BookingStatus status,
                   LocalDateTime createdAt) {
        this.bookingId = bookingId;
        this.studentId = studentId;
        this.experimentId = experimentId;
        this.bookingDate = bookingDate;
        this.slot = slot;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public int getSlot() {
        return slot;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getSlotLabel() {
        String[] labels = {"09:00-10:00", "10:00-11:00", "11:00-12:00", "13:00-14:00", "14:00-15:00", "15:00-16:00"};
        return labels[slot - 1];
    }

    @Override
    public String toString() {
        return bookingId + " | " + studentId + " | " + experimentId + " | "
                + bookingDate + " | Slot " + slot + " (" + getSlotLabel() + ") | " + status;
    }
}
