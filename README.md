# Job Market Intelligence

A backend-first platform for analyzing software job market trends.

The system collects job listings, extracts requested technologies, stores structured data in PostgreSQL, and exposes market intelligence through a REST API. A React dashboard will be added after the backend MVP is complete.

## Project Goal

The goal of this project is to answer questions such as:

* Which technologies are most frequently requested in software jobs?
* Which technologies are commonly requested together?
* What technologies are requested for specific roles?
* How do technology requirements vary by location and experience level?
* How does demand for technologies change over time?

## Current Status

🚧 **Early development**

Milestone 1 (core backend + DB + Jobs API) is complete: the Spring Boot backend connects to PostgreSQL via Flyway migrations, the `Job`/`Technology` many-to-many relationship is modeled and seeded, and `GET /api/jobs` (with `location`, `technology`, `experienceLevel`, `employmentType` filters) and `GET /api/jobs/{id}` are working, returning DTOs rather than raw entities.

Milestone 2 (JSON ingestion + normalization + dictionary extraction) is also complete: `data/jobs.json` holds free-text job postings, which are normalized (locations, employment type, experience level) and scanned by a dictionary-based technology extractor before being persisted, via `POST /api/admin/import/jobs`. Imports are idempotent on `sourceUrl` (repeats are skipped).

Milestone 3 (analytics API) and Milestone 4 (React dashboard) are also complete: all six analytics endpoints are implemented and the dashboard renders each one as a live chart, with no hard-coded data.

Milestone 5 (real-world ingestion) is also complete: a `JobSourceAdapter` interface keeps ingestion source-agnostic, and a `JoobleJobSourceAdapter` (`POST /api/admin/import/jooble`) pulls in genuine, live job postings. Scraping LinkedIn or Kariyer.net directly was never an option (both forbid it in their ToS and block it technically); the Jooble key issued for Turkey also turned out to have no working Turkey inventory on either of its gateways (see `ROADMAP.md` Milestone 5 for the full diagnosis). Since this is a portfolio project, the resolution is to run Jooble as a general/English-market real-world source instead — `data/jobs.json` remains the Turkey-focused seed data, and Jooble demonstrates real, live external ingestion.

The whole stack (Postgres, backend, frontend) is also Dockerized — see **Running with Docker** below.

## Running with Docker

The whole stack — Postgres, the Spring Boot backend, and the React frontend — runs with Docker Compose. No local Java, Maven, Node, or Postgres install needed.

```bash
cp .env.example .env   # fill in JOOBLE_API_KEY if you have one; safe to leave blank
docker compose up --build
```

- Frontend dashboard: http://localhost:5173
- Backend API: http://localhost:8080
- Postgres is seeded automatically on first boot via the Flyway migrations in `backend/src/main/resources/db/migration` (no manual DB setup).
- `data/jobs.json` is mounted read-only into the backend container; trigger an import with `curl -X POST http://localhost:8080/api/admin/import/jobs`.
- `.env` is gitignored — `POSTGRES_PASSWORD` and `JOOBLE_API_KEY` never get committed or baked into the images (they're passed in as environment variables at container start, not read from any properties file inside the image).
- If you already have something else bound to ports 8080 or 5173 (e.g. a local `mvn`/`npm run dev` instance), stop it first or Compose will fail to bind.

## Architecture

```text
Job Data
   │
   ▼
Parser
   │
   ▼
Normalizer
   │
   ▼
Technology Extractor
   │
   ▼
PostgreSQL
   │
   ▼
Spring Boot REST API
   │
   ▼
React Dashboard
```

## Tech Stack

### Backend

* Java 21
* Spring Boot
* Spring Data JPA
* Maven
* Jakarta Validation

### Database

* PostgreSQL
* Flyway

### Frontend

* React
* JavaScript / TypeScript

### Development

* Git
* Docker
* GitHub Actions

## Project Structure

```text
job-market-intelligence/
│
├── backend/        # Spring Boot backend
├── frontend/       # React frontend
├── data/           # Job listing data
├── docs/           # Project documentation
│
├── README.md
├── PROJECT.md
└── ROADMAP.md
```

## Planned Features

### Job Data

* Import job listings from JSON
* Normalize job listing data
* Store structured job information
* Track technologies requested by employers

### Technology Extraction

The initial version will use dictionary-based extraction.

Example:

```text
"Experience with Spring Boot and PostgreSQL required."

                ↓

Spring Boot
PostgreSQL
```

Technology aliases will be normalized to canonical names.

For example:

```text
postgres
postgresql
postgre sql

        ↓

PostgreSQL
```

### REST API

Implemented endpoints:

```text
GET  /api/jobs
GET  /api/jobs/{id}

GET  /api/analytics/technologies
GET  /api/analytics/jobs-by-location
GET  /api/analytics/jobs-by-experience-level
GET  /api/analytics/jobs-by-employment-type
GET  /api/analytics/jobs-over-time
GET  /api/analytics/technology-combinations

POST /api/admin/import/jobs
POST /api/admin/import/jooble
```

### Dashboard

The React frontend will provide visualizations for:

* Most requested technologies
* Technology trends
* Jobs by location
* Jobs by experience level
* Employment types
* Technology combinations

## Development Roadmap

The project will be developed incrementally:

1. Repository and project setup
2. Spring Boot backend and PostgreSQL
3. Database model and job API
4. JSON ingestion and normalization
5. Technology extraction
6. Analytics API
7. React dashboard
8. Real job data sources
9. Testing, Docker and CI/CD
10. Advanced market intelligence

See [`ROADMAP.md`](ROADMAP.md) for the detailed development plan.

## Development Principles

* Backend and data model first, UI later
* Keep ingestion separate from the web layer
* Use normalized relational data
* Use DTOs for API responses
* Keep technology names canonical and consistent
* Prefer simple solutions before introducing additional infrastructure
* Add complexity only when the project actually requires it

## Future Improvements

Possible future additions include:

* Real job-source integrations
* Automated job collection
* Redis caching
* Advanced trend analysis
* Role-specific technology recommendations
* Technology demand forecasting
* Docker-based deployment
* CI/CD
* Authentication and authorization

## License

This project is currently for educational and portfolio purposes.
