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

## License

This project is currently for educational and portfolio purposes.
