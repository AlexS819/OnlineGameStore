# Online Game Store Management System

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-green.svg)](https://spring.io/projects/spring-boot)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-blue.svg)](https://openjfx.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A professional enterprise-grade desktop retail management suite for digital asset management, license distribution, and operational sales ledger tracking. Engineered strictly following **MVVM architecture** and clean separation of concerns (Layered Architecture) for ultimate maintainability.

---

## Interface & Experience

> The application features a modern, aesthetic dashboard utilizing standard desktop best-practices with dynamic visual feedbacks, safe role-based authorization workflows, and real-time transactional logic.

* **Dynamic Table Views**: Fully sortable columns with visually color-coded inventory state monitoring.
* **Advanced Filtering**: Real-time full-text search integrated with multi-select dimensional tags for granular catalog lookup.
* **Non-Blocking Execution**: Background threaded synthesis utilizing concurrent task frameworks ensures zero interface latency during intensive operations.

---

## Core Capabilities

### Security & RBAC
- **Role-Based Access Control**: Distinct visual pathways and operational barriers separating standard client subjects from administrative staff.
- **Cryptographic Integrity**: Industrial `BCrypt` adaptive hashing applied unconditionally to sensitive credential hashes.
- **Identity Management**: Integrated self-service registration and secured user account life-cycle handlers.

### Inventory Operations (Admin)
- **Full CRUD Matrix**: Manage global distributions of Games, Publishers, and Genre classification systems.
- **Live Stock Management**: Centralized ledger track status of activation codes mapped to specific inventory SKUs.

### Retail Workflow
- **Single-Click Fulfillment**: Linear transactional acquisition routines which safely assign and consume available inventory strings.
- **Automated Record Keeping**: Automatic temporal and financial audit trailing into consolidated order logs.

### Reporting Matrix
- **Excel Ledger Dispatch**: Immediate batch data extraction of entire operational grids driven by **Apache POI**.
- **Branded Receipt Synthesis**: Instantly generate localized financial documentation (.xlsx) specifically formatted for end-user consumption.

---

## Technical Architecture

Built on solid, cloud-native and enterprise frameworks:

| Domain | Technology Stack |
|---|---|
| **Logic Kernel** | Java 21 (LTS) |
| **Framework** | Spring Boot (Dependency Injection / Transactions) |
| **Interface Layer** | JavaFX 21 + FXML (SceneBuilder compliant) |
| **Presentation** | Model-View-ViewModel (MVVM) |
| **Persistence** | Spring Data JPA + Hibernate |
| **Database** | H2 (Standalone Local Deployment) |
| **Migrations** | Flyway (Deterministic DB baseline seeding) |
| **Documents** | Apache POI (High-fidelity Office XML serialization) |

---

## Deployment Instructions

### 1. Local Execution
Requirements: **JDK 21+** and **Apache Maven**.

Clone and boot immediately using the embedded database engine:
```bash
git clone https://github.com/AlexS819/OnlineGameStore.git
cd OnlineGameStore
mvn spring-boot:run
```

### 2. Building Native Installers
The system utilizes `jpackage` for packaging natively executable deployments without demanding target systems to pre-install Java Runtimes.

Generate standard executable binary:
```bash
mvn clean package
```

---

## Configuration (Environment Variables)

The application relies on environment variables for sensitive integrations (such as email dispatch for 2FA and LiqPay payments). 

For local development or compiled deployments, you must provide these variables. You can either place a `.env` file in the root directory (next to the executable or in the project root) or configure them directly as **System Environment Variables**.

**Required Keys (`.env` example):**
```ini
# SMTP Email configuration (Gmail)
SMTP_USERNAME={email@gmail.com}
SMTP_PASSWORD={password}

# LiqPay Sandbox Payment Configuration
LIQPAY_PUBLIC_KEY={sandbox_...}
LIQPAY_PRIVATE_KEY={sandbox_...}
```

---

## Developer Manifest

```text
src/main/java/com/sochka/onlinegamestore/
├── controller      # View Controllers managing visual triggers and alerts
├── viewmodel       # Presentation logics holding live state observables
├── domain          # Persistence entity modeling representing SQL schemas
├── dto             # Contract Data Transfer Objects powering layer traversal
├── service         # Discrete business behavior definitions and interfaces
├── infrastructure  # Technical support mechanisms (Hashing, Email, Reporting)
└── ui              # Core bootstrap routines for Spring -> JavaFX lifecycle
```