package com.canik.jmi.job;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public List<JobResponse> getAllJobs(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String technology,
            @RequestParam(required = false) String experienceLevel,
            @RequestParam(required = false) String employmentType) {
                return jobService.getAllJobs(location, technology, experienceLevel, employmentType);
            }
    

   @GetMapping("/{id}")
   public JobResponse getJobById(@PathVariable Long id) {
       return jobService.getJobById(id);
   }
   
}