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

The repository currently contains the initial project structure. Backend development will begin with the database model and REST API.

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

Planned endpoints include:

```text
GET /api/jobs
GET /api/jobs/{id}

GET /api/analytics/technologies
GET /api/analytics/jobs-by-location
GET /api/analytics/jobs-by-experience-level
GET /api/analytics/jobs-over-time
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
