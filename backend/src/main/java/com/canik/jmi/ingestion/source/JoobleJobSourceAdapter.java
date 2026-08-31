package com.canik.jmi.ingestion.source;

import com.canik.jmi.ingestion.JobImportRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Fetches job postings from Jooble's REST API, filtered to Turkey via the
 * `location` search parameter (e.g. "Istanbul", "Turkey", "Ankara").
 *
 * The key was issued through Jooble's Turkey signup page (tr.jooble.org),
 * but that subdomain's own /api/{key} endpoint returns an app-level 403
 * ("only registered users") even with a real browser User-Agent - the key
 * only works against the general gateway, https://jooble.org/api/{key}.
 *
 * Jooble's free key has a lifetime cap of 500 requests total (not monthly),
 * so this adapter is only ever called on demand via the admin import
 * endpoint - never on a schedule - and each call is one page of results.
 *
 * Jooble's `salary` field is unstructured free text (not a min/max pair),
 * so it is intentionally left unparsed here; salaryMin/salaryMax stay null
 * for Jooble-sourced jobs.
 */
@Component
public class JoobleJobSourceAdapter implements JobSourceAdapter {

    private static final Logger log = LoggerFactory.getLogger(JoobleJobSourceAdapter.class);
    private static final int MAX_ATTEMPTS = 3;

    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();

    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String baseUrl;

    public JoobleJobSourceAdapter(
            ObjectMapper objectMapper,
            @Value("${app.jooble.api-key:}") String apiKey,
            @Value("${app.jooble.base-url:https://jooble.org/api/}") String baseUrl) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    @Override
    public String sourceName() {
        return "JOOBLE";
    }

    @Override
    public List<JobImportRecord> fetchJobs(JobFetchCriteria criteria) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                "Jooble API key is not configured. Set the JOOBLE_API_KEY environment variable.");
        }

        JoobleSearchResponse response = search(criteria);
        log.info("Jooble search for keywords='{}' location='{}' page={} returned {} of {} total results",
            criteria.keywords(), criteria.location(), criteria.page(),
            response.jobs() == null ? 0 : response.jobs().size(), response.totalCount());

        return (response.jobs() == null ? List.<JoobleJob>of() : response.jobs()).stream()
            .filter(this::hasRequiredFields)
            .map(this::toImportRecord)
            .toList();
    }

    private boolean hasRequiredFields(JoobleJob job) {
        return notBlank(job.title()) && notBlank(job.company()) && notBlank(job.link());
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    private JobImportRecord toImportRecord(JoobleJob job) {
        return new JobImportRecord(
            job.title(),
            job.company(),
            job.location(),
            job.snippet() == null ? "" : job.snippet(),
            job.type(),
            null,
            null,
            null,
            null,
            parsePostedAt(job.updated()),
            sourceName(),
            job.link()
        );
    }

    private LocalDateTime parsePostedAt(String updated) {
        if (updated == null || updated.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(updated);
        } catch (DateTimeParseException ignored) {
            // fall through to date-only parsing
        }
        try {
            return LocalDate.parse(updated, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        } catch (DateTimeParseException ex) {
            log.warn("Could not parse Jooble 'updated' date '{}', leaving postedAt null", updated);
            return null;
        }
    }

    private JoobleSearchResponse search(JobFetchCriteria criteria) {
        String requestBody = objectMapper.writeValueAsString(
            new JoobleSearchRequest(criteria.keywords(), criteria.location(), criteria.page()));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + apiKey))
            .header("Content-Type", "application/json")
            .timeout(Duration.ofSeconds(15))
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        long backoffMs = 500;
        RuntimeException lastFailure = null;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), JoobleSearchResponse.class);
                }
                if (response.statusCode() == 429 || response.statusCode() >= 500) {
                    lastFailure = new IllegalStateException(
                        "Jooble API returned status " + response.statusCode() + ", attempt " + attempt);
                } else {
                    throw new IllegalStateException(
                        "Jooble API returned status " + response.statusCode() + ": " + response.body());
                }
            } catch (java.io.IOException e) {
                lastFailure = new IllegalStateException("Jooble API request failed: " + e.getMessage(), e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Jooble API request interrupted", e);
            }

            if (attempt < MAX_ATTEMPTS) {
                sleep(backoffMs);
                backoffMs *= 2;
            }
        }

        throw lastFailure;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private record JoobleSearchRequest(String keywords, String location, int page) {
    }

    private record JoobleSearchResponse(Integer totalCount, List<JoobleJob> jobs) {
    }

    private record JoobleJob(
        String id,
        String title,
        String location,
        String snippet,
        String salary,
        String source,
        String type,
        String link,
        String company,
        String updated
    ) {
    }
}
