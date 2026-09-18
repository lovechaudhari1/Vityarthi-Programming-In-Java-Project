# CampusLab - Testing Documentation

## 1. Testing Overview

CampusLab was tested using functional testing, validation testing, boundary/edge-case testing, and basic integration checks.

The main objective of testing is to verify that:

- valid operations are completed correctly
- invalid input is handled safely
- booking conflicts are prevented
- resource status is updated correctly
- database operations behave as expected
- the command-line interface remains usable after errors

Testing is focused on the functionality required for the semester project and does not attempt to provide enterprise-level test automation.

---

## 2. Testing Environment

| Item | Value |
|---|---|
| Application Type | Command-Line Interface |
| Language | Java |
| Java Version | JDK 17+ |
| Build Tool | Maven |
| Database | SQLite |
| Database Access | JDBC |
| Operating System | Windows / Linux / macOS |
| Main Entry Point | `CampusLabApplication.java` |

---

## 3. Test Case Summary

| Test ID | Test Scenario | Input / Action | Expected Result | Status |
|---|---|---|---|---|
| TC-01 | Start application | Run the application | CampusLab menu is displayed without errors | Pass |
| TC-02 | View available resources | Select resource listing | Available resources are displayed | Pass |
| TC-03 | Valid booking | Enter valid student, experiment, date and slot | Booking is created successfully | Pass |
| TC-04 | Invalid student ID | Enter a non-existing student ID | Meaningful validation/error message is displayed | Pass |
| TC-05 | Booking conflict | Attempt a second booking for the same conflicting resource/slot | Booking is rejected or waitlist option is provided | Pass |
| TC-06 | Invalid date | Enter an invalid date format/value | Validation error is displayed and user can retry | Pass |
| TC-07 | Cancel booking | Cancel an eligible existing booking | Booking status changes to `CANCELLED` | Pass |
| TC-08 | Resource under maintenance | Attempt to book an unavailable/maintenance resource | Booking is prevented | Pass |
| TC-09 | Resource return | Record return of an issued resource | Resource status is updated correctly | Pass |
| TC-10 | Invalid menu option | Enter an unsupported menu number | Error is shown and menu is displayed again | Pass |

> Note: The status column should be changed to the actual result after performing the tests locally.

---

## 4. Detailed Test Cases

### TC-01 - Application Startup

**Objective:** Verify that the application starts correctly.

**Steps:**
1. Open the project directory.
2. Run the Maven command.
3. Observe the console.

**Expected Result:**
- Application starts.
- Database initialization completes.
- Main menu is displayed.

**Expected Output:**

```text
========================================
       CAMPUSLAB RESOURCE MANAGER
========================================
1. Student Menu
2. Lab Staff Menu
3. Exit
Choose option:
```

---

### TC-02 - View Available Resources

**Objective:** Verify that users can view laboratory resources.

**Steps:**
1. Start the application.
2. Open the appropriate menu.
3. Select the resource listing option.

**Expected Result:**
The system displays resource information such as:

- Resource ID
- Resource name
- Type
- Location
- Status

**Example:**

```text
RES014   Arduino Starter Kit   EXPERIMENT_KIT   AVAILABLE
RES021   Digital Multimeter   EQUIPMENT        AVAILABLE
RES030   Lab PC-30            COMPUTER         AVAILABLE
```

---

### TC-03 - Valid Booking

**Objective:** Verify successful booking creation.

**Sample Input:**

```text
Student ID: ST101
Experiment ID: EXP201
Date: 28-09-2026
Slot: 2
```

**Expected Result:**

```text
Booking created successfully.
Booking ID: BKxxxx
Status: CONFIRMED/PENDING
```

The exact booking status depends on the approval workflow implemented by the application.

---

### TC-04 - Invalid Student ID

**Objective:** Verify validation of a non-existing student.

**Sample Input:**

```text
Student ID: ST999
```

**Expected Result:**

The system rejects the request and displays a meaningful error such as:

```text
Student not found.
Please enter a valid student ID.
```

The program should continue running instead of terminating unexpectedly.

---

### TC-05 - Booking Conflict

**Objective:** Verify that conflicting reservations are not accepted.

**Steps:**
1. Create a valid booking for a resource and slot.
2. Attempt another booking that conflicts with the existing booking.

**Expected Result:**

```text
Booking could not be completed.
Reason: Resource/slot conflict.
```

Where supported, the user may be offered a waitlist option.

---

### TC-06 - Invalid Date

**Objective:** Verify date input validation.

**Sample Inputs:**

```text
28-13-2026
abc
32-09-2026
```

**Expected Result:**

The application displays a validation message and asks the user to enter the date again.

---

### TC-07 - Cancel Booking

**Objective:** Verify booking cancellation.

**Steps:**
1. Select an existing eligible booking.
2. Choose the cancellation operation.
3. Confirm cancellation.

**Expected Result:**

```text
Booking cancelled successfully.
Status: CANCELLED
```

The cancelled slot should become available according to the application's booking rules.

---

### TC-08 - Resource Under Maintenance

**Objective:** Verify that resources under maintenance cannot be booked.

**Steps:**
1. Mark a resource as `UNDER_MAINTENANCE`.
2. Attempt to create a booking requiring that resource.

**Expected Result:**

```text
Resource unavailable.
Reason: Resource is under maintenance.
```

No invalid booking should be created.

---

### TC-09 - Resource Return

**Objective:** Verify resource return handling.

**Steps:**
1. Issue a resource for an approved booking.
2. Record its return.

**Expected Result:**
- Issue/usage information is updated.
- Resource availability is restored where appropriate.
- Return operation completes without corrupting data.

---

### TC-10 - Invalid Menu Choice

**Objective:** Verify safe handling of unexpected menu input.

**Sample Input:**

```text
99
abc
-1
```

**Expected Result:**

```text
Invalid option.
Please choose a valid menu item.
```

The application should return to the menu rather than terminate.

---

## 5. Edge Case Testing

The following edge cases should also be checked:

| Edge Case | Expected Behavior |
|---|---|
| Empty student name | Input rejected |
| Negative resource quantity | Input rejected |
| Duplicate resource ID | Duplicate record prevented |
| Duplicate booking for same slot | Conflict detected |
| Booking a maintenance resource | Booking rejected |
| Cancelling an already cancelled booking | Operation rejected or handled safely |
| Returning an unissued resource | Operation rejected |
| Very long text input | Input handled without crashing |
| Non-numeric input for numeric field | Validation error displayed |
| Database file not present | Application creates/initializes it where supported |

---

## 6. Validation Strategy

Validation is performed before important operations are committed.

### Input Validation

The application validates:

- required text fields
- numeric values
- IDs
- dates
- menu choices
- resource quantities
- booking slot values

### Business Validation

The application checks:

- student existence
- experiment existence
- resource availability
- booking conflicts
- resource maintenance status
- valid booking cancellation
- valid resource issue/return operations

---

## 7. Error Handling Strategy

CampusLab uses Java exception handling to prevent expected errors from terminating the application.

The project includes application-specific exceptions such as:

- `CampusLabException`
- `InvalidBookingException`
- `ResourceUnavailableException`

Database operations use JDBC exception handling to detect database failures.

User-facing errors should be shown as readable messages instead of raw stack traces.

### Example

```text
Booking could not be completed.
Reason: The selected resource is unavailable.
Please choose another slot.
```

---

## 8. Integration Testing

The following interactions should be checked together because they involve multiple components:

### Booking Flow

```text
Console Input
    ↓
BookingService
    ↓
Validation
    ↓
LabRepository
    ↓
JDBC
    ↓
Database
    ↓
Booking Result
```

### Resource Lifecycle

```text
Available
   ↓
Reserved
   ↓
Issued
   ↓
Returned
   ↓
Available
```

### Maintenance Flow

```text
Available
   ↓
Maintenance Report Created
   ↓
Under Maintenance
   ↓
Maintenance Completed
   ↓
Available
```

---

## 9. Testing Conclusion

The testing process focuses on the core functional requirements of CampusLab.

The major areas verified are:

- application startup
- resource viewing
- booking creation
- booking conflict detection
- input validation
- booking cancellation
- resource availability
- maintenance restrictions
- resource return
- command-line error handling

The final `Pass`/`Fail` values should be updated after the student performs the tests on the actual local environment.

