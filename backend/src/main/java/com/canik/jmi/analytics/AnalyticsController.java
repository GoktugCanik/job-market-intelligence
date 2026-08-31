package com.canik.jmi.analytics;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/technologies")
    public List<TechnologyCountResponse> getTopTechnologies(@RequestParam(defaultValue = "10") int limit) {
        return analyticsService.getTopTechnologies(limit);
    }

    @GetMapping("/jobs-by-location")
    public List<LocationCountResponse> getJobsByLocation() {
        return analyticsService.getJobsByLocation();
    }

    @GetMapping("/jobs-by-experience-level")
    public List<ExperienceLevelCountResponse> getJobsByExperienceLevel() {
        return analyticsService.getJobsByExperienceLevel();
    }

    @GetMapping("/jobs-by-employment-type")
    public List<EmploymentTypeCountResponse> getJobsByEmploymentType() {
        return analyticsService.getJobsByEmploymentType();
    }

    @GetMapping("/jobs-over-time")
    public List<JobsOverTimeResponse> getJobsOverTime(@RequestParam(defaultValue = "month") String granularity) {
        return analyticsService.getJobsOverTime(granularity);
    }

    @GetMapping("/technology-combinations")
    public List<TechnologyCombinationResponse> getTechnologyCombinations(@RequestParam(defaultValue = "10") int limit) {
        return analyticsService.getTechnologyCombinations(limit);
    }
}
