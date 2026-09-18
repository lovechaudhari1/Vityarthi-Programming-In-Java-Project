# Project Statement

## Problem Statement

College laboratories often have limited numbers of computers, kits, instruments, and other shared resources. When bookings are managed using notebooks, spreadsheets, or verbal communication, students may not know the current availability and staff may have difficulty identifying booking conflicts or tracking resource usage.

CampusLab provides a simple command-line system for managing laboratory resources, experiments, student bookings, resource status, maintenance, and basic reports.

## Scope

The first version covers student booking, resource and experiment management, booking approval/cancellation, maintenance status, file export, backup, and JDBC-based persistent storage.

The project intentionally does not include a web frontend, cloud deployment, Spring Boot, mobile development, or external notification services.

## Target Users

- Students who need laboratory resources or experiment slots.
- Laboratory staff who manage resources, approve bookings, and maintain usage records.

## High-Level Features

1. Student resource and experiment browsing.
2. Booking request and conflict validation.
3. Staff approval/rejection of booking requests.
4. Resource status and maintenance management.
5. Booking history and simple reports.
6. CSV export and file-based backup.
7. JDBC persistence using a local SQLite database.
8. Core Java OOP, collections, exception handling, file I/O, packages, and multithreading.
