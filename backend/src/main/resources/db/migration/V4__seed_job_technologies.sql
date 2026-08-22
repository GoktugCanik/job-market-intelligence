INSERT INTO technologies (name, category) VALUES
('Java', 'LANGUAGE'),
('Python', 'LANGUAGE'),
('TypeScript', 'LANGUAGE'),
('Spring Boot', 'FRAMEWORK'),
('React', 'FRAMEWORK'),
('FastAPI', 'FRAMEWORK'),
('PostgreSQL', 'DATABASE'),
('Docker', 'DEVOPS'),
('Git', 'TOOL'),
('HTML', 'LANGUAGE'),
('CSS', 'LANGUAGE'),
('REST APIs', 'CONCEPT');

INSERT INTO job_technologies (job_id, technology_id)
SELECT j.id, t.id
FROM jobs j, technologies t
WHERE j.source_url = 'https://example.com/jobs/1'
  AND t.name IN ('Java', 'Spring Boot', 'PostgreSQL', 'Git');

INSERT INTO job_technologies (job_id, technology_id)
SELECT j.id, t.id
FROM jobs j, technologies t
WHERE j.source_url = 'https://example.com/jobs/2'
  AND t.name IN ('Java', 'Spring Boot', 'PostgreSQL', 'Docker', 'REST APIs');

INSERT INTO job_technologies (job_id, technology_id)
SELECT j.id, t.id
FROM jobs j, technologies t
WHERE j.source_url = 'https://example.com/jobs/3'
  AND t.name IN ('React', 'TypeScript', 'HTML', 'CSS', 'Git');

INSERT INTO job_technologies (job_id, technology_id)
SELECT j.id, t.id
FROM jobs j, technologies t
WHERE j.source_url = 'https://example.com/jobs/4'
  AND t.name IN ('Java', 'Spring Boot', 'React', 'PostgreSQL', 'Docker');

INSERT INTO job_technologies (job_id, technology_id)
SELECT j.id, t.id
FROM jobs j, technologies t
WHERE j.source_url = 'https://example.com/jobs/5'
  AND t.name IN ('Python', 'FastAPI', 'PostgreSQL', 'Docker');