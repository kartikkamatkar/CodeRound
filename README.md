# CodeRound — Full-Stack DSA Coding Assessment Platform

CodeRound is a secure, production-ready, full-stack web application for conducting Data Structures & Algorithms (DSA) technical coding rounds. It models authentic enterprise assessment workflows (such as HackerRank, LeetCode, or CodeSignal).

---

## 🚀 Features

- **4-Question Assessment Rounds**: Each round automatically samples and assigns exactly 4 distinct questions ordered by difficulty (e.g., Easy, Medium, Hard).
- **Session Code Lifecycle**: Generates unique assessment codes (e.g., `CR-ABCD-1234`) with a strict 60-minute countdown timer enforced on the backend.
- **Monaco Code Editor**: Real-time multi-language editor (Java, Python, C++, C, JavaScript) with starter templates.
- **Save & Restore Across Navigation**: Autosaves code per question and preserves drafts as the candidate navigates between questions.
- **Sample Run Execution**: Candidate can run code against sample (public) test cases with instant output, stderr, and compilation diagnostics.
- **Isolated Judge0 CE Evaluation**: Code execution is sandboxed via Judge0 CE. Hidden test cases remain strictly server-side and are never exposed to the client.
- **Final Evaluation & Scoring**: When submitted (or expired), all 4 questions are evaluated against both public and hidden test cases, computing pass rates, execution times, and memory metrics.
- **Results Dashboard**: Detailed submission breakdown showing test case pass counts, execution status (Accepted, Wrong Answer, Time Limit Exceeded, Compilation Error), and overall score.

---

## 🛠️ Technology Stack

- **Backend**: Java 21, Spring Boot 3.3.4, Spring Data JPA, Hibernate, PostgreSQL, Flyway Database Migrations, RestClient, JUnit 5, H2 (test-scoped).
- **Frontend**: React 18, TypeScript, Vite, Tailwind CSS, Monaco Editor (`@monaco-editor/react`), Lucide React Icons.
- **Infrastructure & Deployment**: Docker, Docker Compose, Nginx Reverse Proxy, Judge0 CE.

---

## 📂 Project Structure

```
CodeRound/
├── src/main/java/com/coderound/backend/
│   ├── client/          # Judge0 REST client & DTOs
│   ├── config/          # CORS, RestClient & Judge0 configuration
│   ├── controller/      # REST API Controllers (Assessment, Question, Submission)
│   ├── dto/             # Request & Response DTOs
│   ├── entity/          # JPA Entities (Session, Question, Attempt, Submission, TestCase)
│   ├── enums/           # AssessmentStatus, Difficulty, Language, SubmissionStatus
│   ├── exception/       # Global exception handling & custom errors
│   ├── repository/      # Spring Data JPA repositories
│   └── service/         # Business logic & evaluation services
├── src/main/resources/
│   ├── db/migration/    # Flyway migration scripts (V1 schema, V2 seed questions)
│   └── application.properties
├── frontend/            # React + TypeScript + Vite SPA
│   ├── src/
│   │   ├── api/         # Axios / Fetch client calling CodeRound API
│   │   ├── components/  # Monaco editor, test case runner, timer, results dashboard
│   │   └── types/       # Assessment and code execution TypeScript definitions
│   ├── package.json
│   └── vite.config.ts
├── nginx/
│   └── nginx.conf       # Reverse proxy routing /api -> backend, / -> frontend
├── docker-compose.yml   # Full stack deployment (Postgres, Judge0, Backend, Frontend, Nginx)
├── Dockerfile           # Backend multi-stage build
└── pom.xml
```

---

## 🏃 Running Locally (Development Mode)

### 1. Prerequisites
- **Java 21 JDK**
- **Node.js 20+ & npm**
- **PostgreSQL 16+** running locally (or use Docker for PostgreSQL)

### 2. Configure Database
Ensure a database named `coderound_db` exists:
```sql
CREATE DATABASE coderound_db;
```
Verify your credentials in `src/main/resources/application.properties` or configure environment variables in `.env`.

### 3. Start Backend
Run database migrations and start the Spring Boot API:
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```
The backend starts on `http://localhost:8080`. Flyway automatically creates the schema and seeds 6 classic DSA questions with sample and hidden test cases.

### 4. Start Frontend
```bash
cd frontend
npm install
npm run dev
```
The frontend starts on `http://localhost:5173` (with `/api` automatically proxied to `http://localhost:8080`).

---

## 🐳 Running with Docker Compose (Production Deployment)

To start the entire platform with a single command (PostgreSQL, Redis, Judge0 CE, Spring Boot Backend, Vite Frontend, and Nginx reverse proxy):

```bash
docker-compose up --build -d
```

Access the application in your browser:
- **Application Portal**: `http://localhost`
- **Backend Direct API**: `http://localhost:8080/api/v1`
- **Swagger / OpenAPI Documentation**: `http://localhost:8080/swagger-ui/index.html` (or via `http://localhost/swagger-ui/index.html`)

To stop all services:
```bash
docker-compose down -v
```

---

## 🧪 Automated Testing

CodeRound features comprehensive automated unit and integration tests using JUnit 5 and an in-memory hermetic H2 test database:

```bash
# Windows
.\mvnw.cmd test

# Linux / macOS
./mvnw test
```

### Test Coverage Highlights:
- **Assessment Session Lifecycle**: Tests session creation, unique code generation, 4 distinct questions assignment, order preservation, code saving, and session expiry enforcement.
- **Question Management**: Tests question retrieval, difficulty filtering, public sample test cases filtering (hidden tests are strictly excluded).
- **Submission & Evaluation**: Tests duplicate submission prevention, status transition (`PROCESSING` -> `COMPLETED`), test case pass rate calculation, and score persistence.
- **Judge0 Execution & Offline Simulation**: Tests execution requests, output matching, time limits, memory limits, and graceful fallback simulation when Judge0 CE is offline.

---

## 📋 REST API Endpoints

### Assessments
- `POST /api/v1/assessments` — Start assessment & receive unique session code with 4 assigned questions.
- `GET /api/v1/assessments/{sessionCode}` — Get assessment session details, questions summary, and remaining time.
- `GET /api/v1/assessments/{sessionCode}/questions/{questionId}` — Get question details with sample test cases.
- `PUT /api/v1/assessments/{sessionCode}/questions/{questionId}/code` — Save candidate source code.
- `GET /api/v1/assessments/{sessionCode}/questions/{questionId}/code` — Retrieve saved candidate source code.
- `POST /api/v1/assessments/{sessionCode}/questions/{questionId}/run` — Execute code against sample test cases via Judge0.
- `POST /api/v1/assessments/{sessionCode}/submit` — Submit full assessment for evaluation against all test cases.

### Submissions & Results
- `GET /api/v1/submissions/{id}` — Get submission status and detailed evaluation results.
- `GET /api/v1/assessments/{sessionCode}/submission` — Get submission results for a session code.

### Question Catalog
- `GET /api/v1/questions` — List questions (supports `?difficulty=EASY`).
- `GET /api/v1/questions/{id}` — Get question by ID.
- `GET /api/v1/questions/{id}/test-cases` — Get public test cases for a question.
- `POST /api/v1/questions` — Add a new question with test cases.
