package com.canik.jmi.job;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public List<JobResponse> getAllJobs() {
        return jobService.getAllJobs();
    }

    @GetMapping("/{id}")
    public JobResponse getJobById(@PathVariable Long id) {
        return jobService.getJobById(id);
    }
}   
