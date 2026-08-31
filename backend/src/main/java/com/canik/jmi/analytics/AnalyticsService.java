package com.canik.jmi.analytics;

import com.canik.jmi.job.JobRepository;
import com.canik.jmi.technology.TechnologyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class AnalyticsService {

    private static final Set<String> VALID_GRANULARITIES = Set.of("day", "week", "month");

    private final JobRepository jobRepository;
    private final TechnologyRepository technologyRepository;
    private final AnalyticsRepository analyticsRepository;

    public AnalyticsService(
            JobRepository jobRepository,
            TechnologyRepository technologyRepository,
            AnalyticsRepository analyticsRepository) {
        this.jobRepository = jobRepository;
        this.technologyRepository = technologyRepository;
        this.analyticsRepository = analyticsRepository;
    }

    @Transactional(readOnly = true)
    public List<TechnologyCountResponse> getTopTechnologies(int limit) {
        return technologyRepository.countJobsByTechnology().stream()
            .limit(Math.max(limit, 0))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<LocationCountResponse> getJobsByLocation() {
        return jobRepository.countJobsByLocation();
    }

    @Transactional(readOnly = true)
    public List<ExperienceLevelCountResponse> getJobsByExperienceLevel() {
        return jobRepository.countJobsByExperienceLevel();
    }

    @Transactional(readOnly = true)
    public List<EmploymentTypeCountResponse> getJobsByEmploymentType() {
        return jobRepository.countJobsByEmploymentType();
    }

    @Transactional(readOnly = true)
    public List<JobsOverTimeResponse> getJobsOverTime(String granularity) {
        String normalized = granularity == null
            ? "month"
            : granularity.toLowerCase(Locale.ROOT);
        if (!VALID_GRANULARITIES.contains(normalized)) {
            normalized = "month";
        }
        return analyticsRepository.jobsOverTime(normalized).stream()
            .map(row -> new JobsOverTimeResponse((String) row[0], ((Number) row[1]).longValue()))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<TechnologyCombinationResponse> getTechnologyCombinations(int limit) {
        return analyticsRepository.technologyCombinations().stream()
            .map(row -> new TechnologyCombinationResponse((String) row[0], (String) row[1], ((Number) row[2]).longValue()))
            .limit(Math.max(limit, 0))
            .toList();
    }
}
