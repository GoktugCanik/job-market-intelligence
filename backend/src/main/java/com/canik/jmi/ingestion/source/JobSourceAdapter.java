package com.canik.jmi.ingestion.source;

import com.canik.jmi.ingestion.JobImportRecord;

import java.util.List;

/**
 * Contract for a real job-posting source (an external API, a scraper, a feed).
 * Each adapter is responsible only for fetching and mapping into the same
 * {@link JobImportRecord} shape the local jobs.json import already uses -
 * normalization, technology extraction and idempotent persistence all stay
 * in JobIngestionService, unchanged, regardless of the source.
 */
public interface JobSourceAdapter {

    String sourceName();

    List<JobImportRecord> fetchJobs(JobFetchCriteria criteria);
}
