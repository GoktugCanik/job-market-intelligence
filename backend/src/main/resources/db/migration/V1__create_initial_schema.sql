CREATE TABLE jobs (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    company VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    description TEXT NOT NULL,
    employment_type VARCHAR(50),
    experience_level VARCHAR(50),
    remote_type VARCHAR(50),
    salary_min NUMERIC(12, 2),
    salary_max NUMERIC(12, 2),
    posted_at TIMESTAMP,
    source VARCHAR(100) NOT NULL,
    source_url TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE technologies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(100)
);

CREATE TABLE job_technologies (
    job_id BIGINT NOT NULL,
    technology_id BIGINT NOT NULL,

    PRIMARY KEY (job_id, technology_id),

    CONSTRAINT fk_job_technologies_job
        FOREIGN KEY (job_id)
        REFERENCES jobs(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_job_technologies_technology
        FOREIGN KEY (technology_id)
        REFERENCES technologies(id)
        ON DELETE CASCADE
);