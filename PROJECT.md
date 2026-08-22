# Job Market Intelligence (JMI)

A system that ingests job listings, extracts requested technologies, stores everything in PostgreSQL, and exposes market intelligence via a REST API.

This repository follows the “data model + backend first” principle. The React dashboard comes **after the MVP backend is complete**.

---

## Goal

Ingest job listings → extract technologies → persist in PostgreSQL → serve analytics via REST API.

---

## MVP scope (v0)

### Included
- Ingest job listing data (initially from `data/jobs.json`)
- Extract technology terms from job descriptions (dictionary-based)
- Persist data into PostgreSQL
- Expose REST API for:
  - Listing jobs / fetching a single job
  - Basic filters (location, technology, experience level, employment type)
  - Core analytics outputs (e.g., technology frequency)

### Explicitly out of scope (for MVP)
- LLM/AI-based extraction
- Kafka / event-driven architecture
- Redis caching
- Microservices
- Authentication/authorization
- Scraping (LinkedIn etc.) — planned for later milestones

---

## Tech stack

- Backend: Java 21 (or 17) + Spring Boot + Maven
- Persistence: Spring Data JPA + PostgreSQL
- Validation: Jakarta Validation
- Dev productivity: Lombok, Spring Boot DevTools
- Frontend: React (after MVP)

---

## Repository structure

Recommended top-level layout:

```
job-market-intelligence/
├── backend/
├── frontend/        # after MVP
├── data/            # jobs.json etc.
├── docs/
├── PROJECT.md
├── ROADMAP.md
└── README.md
```

---

## Domain model

### Core entities

**Job**
- `id`
- `title`
- `company`
- `location`
- `description`
- `employmentType` (enum recommended)
- `experienceLevel` (enum recommended)
- `salaryMin`, `salaryMax` (optional)
- `postedAt` (optional)
- `source`
- `sourceUrl`
- `createdAt`

**Technology**
- `id`
- `name` (unique)
- `category` (optional; can be normalized later)

**Relationship**
- Job ↔ Technology: many-to-many (`job_technologies`)

> Do not store technologies as a comma-separated string on `Job`. Analytics depends on proper relationships.

---

## Initial DB schema proposal

Table naming is illustrative; actual naming can be driven by JPA mappings.

**jobs**
- `id` (PK)
- `title`
- `company`
- `location`
- `description`
- `employment_type`
- `experience_level`
- `salary_min`
- `salary_max`
- `posted_at`
- `source`
- `source_url`
- `created_at`

**technologies**
- `id` (PK)
- `name` (unique)
- `category`

**job_technologies**
- `job_id` (FK → jobs.id)
- `technology_id` (FK → technologies.id)
- unique constraint: (`job_id`, `technology_id`)

---

## API (MVP)

### Jobs
- `GET /api/jobs`
  - Optional filters: `location`, `technology`, `experienceLevel`, `employmentType`
- `GET /api/jobs/{id}`

### Analytics (first endpoint)
- `GET /api/analytics/technologies`
  - Example response:
    ```json
    [
      { "technology": "Java", "count": 152 },
      { "technology": "Python", "count": 137 }
    ]
    ```

---

## Data ingestion (MVP approach)

The first goal is an end-to-end working system that does not depend on scraping:

`data/jobs.json` → JSON parser → `Job` objects → `Technology extractor` → PostgreSQL

---

## Technology extraction (v0)

Dictionary-based approach:

- Canonical technology name → aliases/variants
  - `Spring Boot`: `spring boot`, `springboot`
  - `PostgreSQL`: `postgres`, `postgresql`, `postgre sql`
  - `React`: `react`, `react.js`, `reactjs`

Extraction rules (recommended):
- Case-insensitive matching
- Basic normalization (punctuation cleanup) and/or word boundaries
- Count each technology at most once per job

---

## Milestone 1 target

- GitHub repository structure
- Spring Boot backend skeleton
- PostgreSQL connection
- `Job` and `Technology` entities
- Many-to-many relationship
- Repository / Service / Controller layers
- `GET /api/jobs`
- `GET /api/jobs/{id}`
- DB seed with 20–50 fake jobs

---

## Engineering principles

- “Model first, then API, UI last”
- Keep ingestion code separate from the web layer (controller)
- Analytics endpoints should return DTOs, not entities
- Normalize alias variants (e.g., `postgres` → `PostgreSQL`) consistently
