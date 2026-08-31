package com.canik.jmi.job;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {
    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getAllJobs(String location, String technology, String experienceLevel, String employmentType) {
        String locationPattern = (location == null) ? null : "%" + location + "%";
        return jobRepository.findByFilters(locationPattern, technology, experienceLevel, employmentType).stream()
            .map(JobResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public JobResponse getJobById(Long id) {
        return jobRepository.findByIdWithTechnologies(id)
            .map(JobResponse::from)
            .orElseThrow(() -> new RuntimeException("Job not found" + id));
    }   
}