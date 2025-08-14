# Playwright API Java 21 CRUD Framework

## Overview
This framework is a **Java 21 + Maven + Playwright** API testing solution with full CRUD capabilities and a smoke test suite for rapid validation.  
It integrates **POJO models**, **Builder pattern**, and a **UseApiSteps** abstraction for clean, maintainable test flows.

---

## Framework Structure

```
.
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── api
│   │   │   │   ├── pojo        # POJO classes for request/response bodies
│   │   │   │   ├── builder     # Builder classes for POJO objects
│   │   │   │   └── steps       # UseApiSteps with reusable API call logic
│   │   │   ├── config          # Config class for environment variables & defaults
│   │   │   └── utils           # Utilities (HTTP requests, constants, helpers)
│   │   └── resources           # application.properties & configs
│   ├── test
│   │   ├── java
│   │   │   └── smokeTests      # Smoke test pack
│   │   └── resources           # Test data & TestNG XMLs
├── pom.xml
└── README.md
```

---

## Technologies Used
- **Java 21** – Core programming language
- **Maven** – Build & dependency management
- **Playwright for Java** – HTTP client for API testing
- **TestNG** – Test execution & grouping
- **Lombok** – Reduces boilerplate in POJO classes
- **Jackson** – JSON serialization/deserialization
- **Rest-Assured** – HTTP request handling for extended flows
- **Allure** – Test reporting
- **GitHub Actions** – CI/CD integration

---

## Test Packs

### Smoke Test Suite
Contains the **critical path** tests for API validation:

1. **`smokeTests.ApiSmokeTests`**  
   - General API availability checks  
   - Verifies main endpoints respond with correct status codes and structure  

2. **`smokeTests.AuthRegisterTests`**  
   - Tests user registration scenarios  
   - Covers both positive and negative flows  

3. **`smokeTests.AuthSmokeTests`**  
   - Authentication endpoint checks  
   - Token retrieval and validation scenarios  

---

## How to Run Tests

### Prerequisites
- Install **Java 21**
- Install **Maven 3.9+**
- (Optional) Install **Allure** CLI for local report viewing

### Run All Smoke Tests
```bash
mvn clean test   -Dtest=smokeTests.ApiSmokeTests,smokeTests.AuthRegisterTests,smokeTests.AuthSmokeTests
```

### Run Using TestNG Suite
If you have a `TestStagePropertiesTestNG.xml` file in `src/test/resources`:
```bash
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/TestStagePropertiesTestNG.xml
```

---

## Generating Reports
After running tests:
```bash
mvn allure:serve
```

---

## CI/CD
The project includes a **GitHub Actions** workflow to run smoke tests automatically on pushes and pull requests to `main` or `master`.

---
