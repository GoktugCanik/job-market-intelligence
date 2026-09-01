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

**Status:** Complete

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
- [x] The application starts and connects to PostgreSQL.
- [x] `GET /api/jobs` and `GET /api/jobs/{id}` work, with optional `location`, `technology`, `experienceLevel`, `employmentType` filters.
- [x] Job technologies are stored as relationships in DB (not as a single comma-separated string column).
- [ ] DB seed is only 5 jobs so far, below the 20–50 target — can be expanded later or superseded by Milestone 2's JSON ingestion.

---

## Milestone 2 — JSON ingestion + normalization + dictionary extraction

**Status:** Complete

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
- [x] `jobs.json` can be imported and persisted into DB.
- [x] Technology extraction is persisted with correct relationships.

**Implementation notes**
- `data/jobs.json` holds free-text `description` fields only (no explicit technologies array); technologies are recovered purely by dictionary extraction, matching how a real scraped source would look.
- Idempotency decision: a repeated `sourceUrl` means **skip** (the existing row is left untouched), not update.
- Triggered manually via `POST /api/admin/import/jobs`, which returns an `ImportSummary` (`totalRecords`, `imported`, `skippedExisting`, `technologiesCreated`).

---

## Milestone 3 — Analytics API (fuel for the dashboard)

**Status:** Complete

**Goal:** The backend should generate market intelligence even before any UI exists.

**Endpoints (implemented)**
- `GET /api/analytics/technologies` (top technologies)
- `GET /api/analytics/jobs-by-location`
- `GET /api/analytics/jobs-by-experience-level`
- `GET /api/analytics/jobs-by-employment-type`
- `GET /api/analytics/jobs-over-time` (weekly/monthly)
- `GET /api/analytics/technology-combinations` (most common co-occurrences)

**Acceptance criteria**
- [x] Each endpoint returns DTOs (not entities).
- [x] PostgreSQL aggregation queries produce correct counts.

---

## Milestone 4 — React Dashboard (first UI)

**Status:** Complete

**Goal:** UI should only visualize API outputs; business logic stays in the backend.

**Work items**
- [x] Create React app
- [x] Connect to analytics endpoints
- [x] Charts (e.g., bar chart, line chart)

**Dashboard widgets (MVP UI)**
- [x] Most requested technologies
- [x] Jobs by location
- [x] Jobs by experience level
- [x] Jobs by employment type
- [x] Jobs over time
- [x] Most common technology combinations

**Acceptance criteria**
- [x] Each widget calls the backend and renders a chart.
- [x] No hard-coded demo datasets.

---

## Milestone 5 — Ingestion from real sources (fetcher/scraper)

**Status:** Complete

**Goal:** Make it easy to add and maintain multiple job sources, without scraping sites (like LinkedIn or Kariyer.net) that forbid it in their ToS and actively block it technically.

**Work items**
- [x] Source adapter interface/contract (`JobSourceAdapter`, `JobFetchCriteria`) so ingestion, normalization, extraction and idempotent persistence stay source-agnostic
- [x] `JoobleJobSourceAdapter` — calls Jooble's REST API with retries/backoff, wired to `POST /api/admin/import/jooble`

**Resolved issue — Jooble has no Turkey inventory under this key**

Confirmed by direct API testing (2026-09-01): the Jooble key (issued via the Turkey signup page, tr.jooble.org) only works against the general gateway, `https://jooble.org/api/{key}` — the Turkey-specific `tr.jooble.org/api/{key}` endpoint 403s the same key. The general gateway has no Turkey inventory under this key: `location` values of "Istanbul", "Ankara", "Izmir", "TR" and "Turkiye" all return `totalCount: 0`, and "Turkey" only matches the US town of Turkey, NC — not the country. This is a data-source/account limitation, not an adapter bug — no `location`/`keywords` combination surfaces real Turkish postings through this key.

Since this is a portfolio project where a working, real-time ingestion pipeline matters more than strict geographic scope, the resolution is to run Jooble as a general/English-market real-world source rather than a Turkey-only one: `POST /api/admin/import/jooble` now defaults `location` to empty (dropping the misleading "Turkey" default that silently matched a US town), and pulls in genuine, live global software-job postings. `data/jobs.json` stays the Turkey-focused seed data; Jooble demonstrates the real-source integration end of the pipeline. Revisit only if a working `tr.jooble.org` key becomes available.

**Acceptance criteria**
- [x] At least one real source integrated (while respecting legal/ethical boundaries).
- [x] Pipeline is modular enough to add another source with minimal changes (proven by the adapter interface).

---

## Milestone 6 — Performance, caching, security, quality

This is the “productization” phase after MVP.

**Status:** In progress — Docker done, everything else still open

**Work items**
- [x] Docker + docker-compose (Postgres + Spring Boot backend + React frontend, `docker compose up --build`)
- [ ] Redis caching for hot analytics
- [ ] Auth: ADMIN / USER (minimal)
- [ ] Tests:
  - Unit (JUnit, Mockito)
  - Integration
  - Testcontainers
- [ ] CI/CD (GitHub Actions):
  - test → build → docker image → deploy

**Docker implementation notes**
- Three services: `db` (`postgres:17-alpine`), `backend` (multi-stage Maven build → `eclipse-temurin:21-jre-alpine`), `frontend` (multi-stage `node:20-alpine` Vite build → `nginx:1.27-alpine` static serve).
- `backend/src/main/resources/application.properties` is gitignored (holds real local secrets) and deliberately excluded from the Docker build context via `backend/.dockerignore`, so no secret ever gets baked into the image. The container gets every Spring property — including `SPRING_DATASOURCE_*` and `APP_JOOBLE_API_KEY` — purely from environment variables set in `docker-compose.yml`/`.env`.
- Postgres data persists in a named volume (`db-data`); Flyway runs the migrations automatically against the fresh DB on backend startup — no manual schema setup.
- `data/jobs.json` is bind-mounted read-only into the backend container so `POST /api/admin/import/jobs` works without rebuilding the image.
- Verified end-to-end (2026-09-01) on an isolated Compose project: fresh containers, Flyway migrated 3 versions from an empty schema, `GET /api/jobs` returned the 5 seeded jobs, `POST /api/admin/import/jobs` imported the 4 `data/jobs.json` records, and `GET /api/analytics/technologies` returned real counts.

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
