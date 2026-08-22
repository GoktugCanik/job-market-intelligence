# Job Market Intelligence (JMI) Roadmap

This roadmap is designed around a “backend + data model + analytics first, dashboard last” approach.

---

## Milestone 0 — Repository skeleton (Day 0)

**Goal:** The project should not be an empty repo; it should be documented and structured from day one.

**Deliverables**
- Folder structure: `backend/`, `frontend/`, `data/`, `docs/`
- `README.md` (short: purpose, how to run, MVP scope)
- `PROJECT.md`, `ROADMAP.md`

**Acceptance criteria**
- A new contributor can understand the project goals in under 2 minutes.

---

## Milestone 1 — Core backend + DB + Jobs API (MVP core)

**Goal:** PostgreSQL is connected, the model is correct, and jobs can be served via API.

**Work items**
- Spring Boot project (Java 21/17, Maven)
- PostgreSQL connection + migrations strategy (recommended: Flyway or Liquibase)
- Entity design:
  - `Job`
  - `Technology`
  - many-to-many: `Job` ↔ `Technology`
- Layers:
  - Repository
  - Service
  - Controller
- Endpoints:
  - `GET /api/jobs`
  - `GET /api/jobs/{id}`
- DB seed:
  - 20–50 fake jobs
  - related technologies

**Acceptance criteria**
- The application starts and connects to PostgreSQL.
- `GET /api/jobs` and `GET /api/jobs/{id}` work.
- Job technologies are stored as relationships in DB (not as a single comma-separated string column).

---

## Milestone 2 — JSON ingestion + normalization + dictionary extraction

**Goal:** Build a stable “data → DB” ingestion pipeline without depending on scraping.

**Work items**
- Define `data/jobs.json` schema (minimal but extensible)
- JSON parser → `Job` mapping
- Normalization layer (example rules):
  - City names / spelling variants (e.g., `Istanbul` vs `İstanbul`)
  - Map employment type / experience level into enums
  - Technology alias map (e.g., `postgres` → `PostgreSQL`)
- Dictionary-based technology extraction (case-insensitive; do not double count)
- Idempotent imports:
  - Decide whether repeated `sourceUrl` means “update” or “skip” and document it

**Acceptance criteria**
- `jobs.json` can be imported and persisted into DB.
- Technology extraction is persisted with correct relationships.

---

## Milestone 3 — Analytics API (fuel for the dashboard)

**Goal:** The backend should generate market intelligence even before any UI exists.

**Suggested endpoints (incremental)**
- `GET /api/analytics/technologies` (top technologies)
- `GET /api/analytics/jobs-by-location`
- `GET /api/analytics/jobs-by-experience-level`
- `GET /api/analytics/jobs-by-employment-type`
- `GET /api/analytics/jobs-over-time` (weekly/monthly)
- `GET /api/analytics/technology-combinations` (most common co-occurrences)

**Acceptance criteria**
- Each endpoint returns DTOs (not entities).
- PostgreSQL aggregation queries produce correct counts.

---

## Milestone 4 — React Dashboard (first UI)

**Goal:** UI should only visualize API outputs; business logic stays in the backend.

**Work items**
- Create React app
- Connect to analytics endpoints
- Charts (e.g., bar chart, line chart)

**Dashboard widgets (MVP UI)**
- Most requested technologies
- Technology frequency
- Jobs by location
- Jobs by experience level
- Jobs by employment type
- Jobs over time
- Most common technology combinations

**Acceptance criteria**
- Each widget calls the backend and renders a chart.
- No hard-coded demo datasets.

---

## Milestone 5 — Ingestion from real sources (fetcher/scraper)

**Goal:** Make it easy to add and maintain multiple job sources.

**Work items**
- Fetcher → Parser → Normalizer → Extractor → DB pipeline
- Source adapter interface/contract (e.g., `JobSourceAdapter`)
- Basic rate limiting / retries / backoff

**Acceptance criteria**
- At least one real source integrated (while respecting legal/ethical boundaries).
- Pipeline is modular enough to add another source with minimal changes.

---

## Milestone 6 — Performance, caching, security, quality

This is the “productization” phase after MVP.

**Work items**
- Redis caching for hot analytics
- Docker + docker-compose (Spring Boot + Postgres + Redis)
- Auth: ADMIN / USER (minimal)
- Tests:
  - Unit (JUnit, Mockito)
  - Integration
  - Testcontainers
- CI/CD (GitHub Actions):
  - test → build → docker image → deploy

---

## Milestone 7 — Differentiating features

**Goal:** Answer questions like “I want to be a Java Developer in Turkey; what should I learn?” using real data.

**Example deliverables**
- Role-based roadmaps (Java backend, frontend, data)
- Trend analysis (tech up/down over time)
- Segmentation by location

---

## Notes / key decisions

- Choose your migration tool early (Flyway/Liquibase); switching later is costly.
- Technology canonicalization is the core value of the product; quality here is product quality.
- Don’t start with scraping: validate DB + backend + analytics first.
