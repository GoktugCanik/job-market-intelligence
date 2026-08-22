package com.canik.jmi.job;

import com.canik.jmi.technology.TechnologyResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record JobResponse(
    Long id,
    String title,
    String company,
    String location,
    String description,
    String employmentType,
    String experienceLevel,
    String remoteType,
    BigDecimal salaryMin,
    BigDecimal salaryMax,
    LocalDateTime postedAt,
    String source,
    String sourceUrl,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Set<TechnologyResponse> technologies
) {

    public static JobResponse from(Job job) {
        return new JobResponse(
            job.getId(),
            job.getTitle(),
            job.getCompany(),
            job.getLocation(),
            job.getDescription(),
            job.getEmploymentType(),
            job.getExperienceLevel(),
            job.getRemoteType(),
            job.getSalaryMin(),
            job.getSalaryMax(),
            job.getPostedAt(),
            job.getSource(),
            job.getSourceUrl(),
            job.getCreatedAt(),
            job.getUpdatedAt(),
            job.getTechnologies().stream()
                .map(TechnologyResponse::from)
                .collect(Collectors.toSet())
        );
    }
}
