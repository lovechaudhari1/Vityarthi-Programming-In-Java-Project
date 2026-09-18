# CampusLab Design Notes

## Architecture

```mermaid
flowchart TB
    U[Student / Lab Staff] --> UI[Console UI]
    UI --> S[Service Layer]
    S --> R[Repository Layer]
    R --> DB[(SQLite Database)]
    S --> F[File Manager]
    F --> FS[(Reports / Backup Files)]
```

## Booking Workflow

```mermaid
flowchart TD
    A[Student selects experiment] --> B[Select date and slot]
    B --> C[Validate input]
    C --> D{Valid?}
    D -- No --> E[Show error]
    D -- Yes --> F[Check duplicate booking]
    F --> G[Check experiment capacity]
    G --> H[Check required resources]
    H --> I{Available?}
    I -- No --> J[Reject request]
    I -- Yes --> K[Create PENDING booking]
    K --> L[Staff review]
    L --> M{Decision}
    M -- Approve --> N[CONFIRMED]
    M -- Reject --> O[REJECTED]
```

## Main OOP Model

```text
LabResource (abstract)
├── Equipment
└── ComputerResource

User (abstract)
├── Student
└── LabStaff
```

The resource hierarchy demonstrates abstraction, inheritance and runtime polymorphism. The `Reservable` interface represents a capability rather than a family relationship.
