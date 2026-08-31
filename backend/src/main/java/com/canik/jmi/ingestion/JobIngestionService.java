package com.canik.jmi.ingestion;

import com.canik.jmi.ingestion.source.JobFetchCriteria;
import com.canik.jmi.ingestion.source.JobSourceAdapter;
import com.canik.jmi.job.Job;
import com.canik.jmi.job.JobRepository;
import com.canik.jmi.technology.Technology;
import com.canik.jmi.technology.TechnologyRepository;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class JobIngestionService {

    private final JobRepository jobRepository;
    private final TechnologyRepository technologyRepository;
    private final JobNormalizer jobNormalizer;
    private final TechnologyDictionary technologyDictionary;
    private final ObjectMapper objectMapper;
    private final String jobsFilePath;

    public JobIngestionService(
            JobRepository jobRepository,
            TechnologyRepository technologyRepository,
            JobNormalizer jobNormalizer,
            TechnologyDictionary technologyDictionary,
            ObjectMapper objectMapper,
            @Value("${app.import.jobs-file:../data/jobs.json}") String jobsFilePath) {
        this.jobRepository = jobRepository;
        this.technologyRepository = technologyRepository;
        this.jobNormalizer = jobNormalizer;
        this.technologyDictionary = technologyDictionary;
        this.objectMapper = objectMapper;
        this.jobsFilePath = jobsFilePath;
    }

    @Transactional
    public ImportSummary importJobs() {
        return importRecords(readRecords());
    }

    @Transactional
    public ImportSummary importFromSource(JobSourceAdapter adapter, JobFetchCriteria criteria) {
        return importRecords(adapter.fetchJobs(criteria));
    }

    private ImportSummary importRecords(List<JobImportRecord> records) {
        int imported = 0;
        int skipped = 0;
        AtomicInteger technologiesCreated = new AtomicInteger(0);

        // Cache technologies looked up/created during this run so repeated
        // mentions across jobs don't hit the DB or violate the unique constraint.
        Map<String, Technology> technologyCache = new HashMap<>();

        for (JobImportRecord record : records) {
            if (jobRepository.existsBySourceUrl(record.sourceUrl())) {
                skipped++;
                continue;
            }

            Set<String> technologyNames = technologyDictionary.extract(record.description());
            Set<Technology> technologies = new HashSet<>();
            for (String name : technologyNames) {
                Technology technology = technologyCache.computeIfAbsent(name, n ->
                    technologyRepository.findByNameIgnoreCase(n)
                        .orElseGet(() -> {
                            technologiesCreated.incrementAndGet();
                            return technologyRepository.save(Technology.builder().name(n).category("UNKNOWN").build());
                        }));
                technologies.add(technology);
            }

            LocalDateTime now = LocalDateTime.now();
            Job job = Job.builder()
                .title(record.title())
                .company(record.company())
                .location(jobNormalizer.normalizeLocation(record.location()))
                .description(record.description())
                .employmentType(jobNormalizer.normalizeEmploymentType(record.employmentType()))
                .experienceLevel(jobNormalizer.normalizeExperienceLevel(record.experienceLevel()))
                .remoteType(jobNormalizer.normalizeRemoteType(record.remoteType()))
                .salaryMin(record.salaryMin())
                .salaryMax(record.salaryMax())
                .postedAt(record.postedAt())
                .source(record.source())
                .sourceUrl(record.sourceUrl())
                .createdAt(now)
                .updatedAt(now)
                .technologies(technologies)
                .build();

            jobRepository.save(job);
            imported++;
        }

        return new ImportSummary(records.size(), imported, skipped, technologiesCreated.get());
    }

    private List<JobImportRecord> readRecords() {
        File file = new File(jobsFilePath);
        return objectMapper.readValue(file, objectMapper.getTypeFactory()
            .constructCollectionType(List.class, JobImportRecord.class));
    }
}
