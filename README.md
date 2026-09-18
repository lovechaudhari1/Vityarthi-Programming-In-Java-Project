# CampusLab — Laboratory Resource & Experiment Booking Manager

A Core Java command-line semester project for managing college laboratory resources, experiments, bookings, and maintenance.

## 1. Overview

CampusLab helps students check laboratory resources and request experiment slots while allowing lab staff to maintain resources, review booking requests, manage maintenance status, and export simple reports.

The project is intentionally built around the Java syllabus rather than a large framework stack.

## 2. Main Features

### Student module
- View experiments.
- View all resources.
- View currently available resources.
- Request an experiment slot.
- Check booking history.
- Cancel an eligible booking.

### Lab staff module
- View students.
- Add laboratory resources.
- Update resource status.
- View all experiments and bookings.
- Approve or reject pending booking requests.
- Put a resource into maintenance.
- Mark maintenance as completed.
- Export booking reports.
- Run a background backup task.

### Technical features
- Object-oriented design with abstraction, inheritance, interfaces, encapsulation, overloading, overriding, and polymorphism.
- Java collections such as `List`, `Map`, and `Stack`.
- Custom exception handling.
- File I/O using character streams.
- Byte-oriented backup support.
- Multithreading with `Runnable` and synchronized file operations.
- JDBC with SQLite.
- External JDBC configuration using `config/db.properties`.

## 3. Technologies

- Java 17+
- Maven 3.8+
- JDBC
- SQLite
- Command Line Interface
- Git / GitHub

## 4. Project Structure

```text
CampusLab/
├── assets/
├── config/
│   └── db.properties
├── data/
│   ├── backups/
│   ├── exports/
│   └── sample-data/
├── docs/
├── sql/
│   └── schema.sql
├── src/
│   └── main/java/com/campuslab/
│       ├── app/
│       ├── db/
│       ├── enums/
│       ├── exception/
│       ├── interfaces/
│       ├── model/
│       ├── repository/
│       ├── service/
│       └── util/
├── .gitignore
├── LICENSE
├── pom.xml
├── README.md
└── statement.md
```

## 5. Requirements

Install:

- JDK 17 or newer
- Maven 3.8 or newer
- Git

Check installation:

```bash
java -version
mvn -version
git --version
```

## 6. Run the Project

Open a terminal in the repository root.

### Windows Command Prompt / PowerShell

```bash
mvn clean compile exec:java
```

### macOS / Linux

```bash
mvn clean compile exec:java
```

The program creates `campuslab.db` automatically in the project root and seeds small demo records on the first run.

## 7. Demo Accounts

This version uses a simple role menu instead of a password system.

Demo student IDs:

```text
ST101
ST102
```

Demo experiments:

```text
EXP201
EXP202
```

## 8. Time Slots

The application uses six simple lab slots:

| Slot | Label |
|---|---|
| 1 | 09:00–10:00 |
| 2 | 10:00–11:00 |
| 3 | 11:00–12:00 |
| 4 | 13:00–14:00 |
| 5 | 14:00–15:00 |
| 6 | 15:00–16:00 |

## 9. Testing

Basic validation can be performed from the CLI:

- invalid menu values
- invalid student IDs
- invalid dates
- past booking dates
- invalid slot numbers
- duplicate student bookings for the same date and slot
- booking an invalid experiment
- booking when required resources are unavailable
- cancellation of already cancelled/rejected bookings
- staff approval/rejection of pending bookings
- maintenance status changes

See `docs/testing-checklist.md` for the planned test checklist.

## 10. Database

The project uses SQLite through JDBC. The database file is generated locally and ignored by Git.

The SQL schema is stored in:

```text
sql/schema.sql
```

JDBC configuration is stored in:

```text
config/db.properties
```

## 11. File Output

Generated files are stored under:

```text
data/exports/
data/backups/
```

These folders contain `.gitkeep` files so the directory structure remains visible in GitHub before any reports are generated.

## 12. Future Improvements

- Desktop GUI using JavaFX/Swing if permitted by the course.
- More detailed staff authentication.
- Separate department-level laboratory calendars.
- Email or notification integration.
- JPA-based persistence as an optional experiment.
- More advanced reporting and analytics.

## 13. Academic Note

This repository is designed as a 2nd-year B.Tech Core Java semester project. The design deliberately avoids Spring Boot, REST APIs, microservices, cloud deployment, and other technologies that are not necessary for the stated course objective.
