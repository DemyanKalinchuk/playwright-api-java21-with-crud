# Playwright API Java 21 CRUD & Auth Framework

## 📌 Project Overview
This project is an **end-to-end API testing framework** built with **Java 21**, **Maven**, and **TestNG**, integrating **RestAssured** for HTTP requests and **Allure** for reporting.  
It is designed to work with public APIs like **ReqRes** and **JSONPlaceholder** for CRUD and authentication flows.

---

## 📂 Framework Structure

```
src
 ├─ main/java
 │   ├─ api/
 │   │   ├─ builders/              # POJO builders (AuthBuilder, UserBuilder, PostBuilder)
 │   │   ├─ pojo/                  # DTOs for auth, users, posts
 │   │   └─ steps/UseApiSteps      # Central API steps combining builders and HttpRequest
 │   ├─ config/Config.java         # Environment variables & properties loader
 │   └─ utils/
 │       ├─ json/Json.java         # JSON parsing helpers
 │       └─ request/
 │           ├─ HttpRequest.java   # Core RestAssured API client
 │           ├─ Headers.java       # Header management helper
 │           └─ path/
 │               ├─ IPath.java
 │               └─ WorkPath.java  # Enum-based endpoint definitions
 └─ test/java
     ├─ tests/auth/                # Auth endpoints tests (login, register)
     ├─ tests/reqres/              # ReqRes API CRUD tests (users, resources)
     ├─ tests/jsonplaceholder/     # JSONPlaceholder CRUD tests (posts)
     └─ tests/smoke/                # API smoke tests pack
```

---

## 🛠 Technologies Used
- **Java 21** — Modern Java syntax & features
- **Maven** — Build & dependency management
- **TestNG** — Testing framework
- **RestAssured** — API HTTP client
- **Lombok** — Reduces boilerplate for POJOs and Builders
- **Jackson** — JSON (de)serialization
- **Allure** — Reporting with request/response attachments
- **Playwright APIRequestContext** — (Optional) Used for direct Playwright API calls
- **Enum-based Path Management** — Centralized endpoint references
- **Builder Pattern for POJOs** — Clean payload creation

---

## 📦 Test Packs

### 1. **Auth Tests** (`tests/auth`)
- `AuthLoginIT` — Valid login & invalid login (missing password)
- `AuthRegisterIT` — Valid register & invalid register (missing password)

### 2. **ReqRes CRUD Tests** (`tests/reqres`)
- `UsersCrudTest` — Create, Read, Update, Delete user flow
- `ResourcesTest` — List & single resource retrieval

### 3. **JSONPlaceholder CRUD Tests** (`tests/jsonplaceholder`)
- `PostsCrudTest` — Create, Read, Update, Delete post flow

### 4. **Smoke Tests** (`tests/smoke`)
- `ApiSmokeTest` — Basic health checks for endpoints

---

## 🔄 Running the Tests

### 1. Clone the Repository
```bash
git clone <your-repo-url>
cd playwright-api-java21-with-crud
```

### 2. Configure Environment Variables (Optional)
```bash
export BASE_URL=https://reqres.in
export API_TOKEN=reqres-free-v1
```

Or pass via Maven system properties:
```bash
mvn test -DBASE_URL=https://reqres.in -DAPI_TOKEN=reqres-free-v1
```

### 3. Run Specific Test Packs

#### Run All Auth Tests
```bash
mvn test -Dtest=tests.auth.*
```

#### Run ReqRes CRUD Tests
```bash
mvn test -Dtest=tests.reqres.*
```

#### Run JSONPlaceholder Posts CRUD Tests
```bash
mvn test -Dtest=tests.jsonplaceholder.* -DBASE_URL=https://jsonplaceholder.typicode.com
```

#### Run All Smoke Tests
```bash
mvn test -Dtest=tests.smoke.*
```

### 4. Generate Allure Report
```bash
mvn allure:serve
```

---

## 🚀 Features
- CRUD & Auth flows with public APIs
- Centralized endpoint management
- Allure reporting with request/response logs
- Retry logic for unstable endpoints
- Flexible config via env variables or Maven params

---

## 📌 Notes
- Public demo APIs (ReqRes, JSONPlaceholder) do **not persist data** — GET after POST may not return the created entity.
- Use **known IDs** from API docs for GET, PUT, DELETE tests when persistence is required.

---

## 👨‍💻 Contribution
Pull requests are welcome! You can extend:
- More endpoints & negative tests
- Data-driven tests
- UI + API integration tests with Playwright
