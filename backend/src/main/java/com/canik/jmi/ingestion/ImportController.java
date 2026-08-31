package com.canik.jmi.ingestion;

import com.canik.jmi.ingestion.source.JobFetchCriteria;
import com.canik.jmi.ingestion.source.JoobleJobSourceAdapter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/import")
public class ImportController {

    private final JobIngestionService jobIngestionService;
    private final JoobleJobSourceAdapter joobleJobSourceAdapter;

    public ImportController(JobIngestionService jobIngestionService, JoobleJobSourceAdapter joobleJobSourceAdapter) {
        this.jobIngestionService = jobIngestionService;
        this.joobleJobSourceAdapter = joobleJobSourceAdapter;
    }

    @PostMapping("/jobs")
    public ImportSummary importJobs() {
        return jobIngestionService.importJobs();
    }

    @PostMapping("/jooble")
    public ImportSummary importFromJooble(
            @RequestParam(defaultValue = "developer") String keywords,
            @RequestParam(defaultValue = "Turkey") String location,
            @RequestParam(defaultValue = "1") int page) {
        return jobIngestionService.importFromSource(
            joobleJobSourceAdapter, new JobFetchCriteria(keywords, location, page));
    }
}
