package com.canik.jmi.job;

import com.canik.jmi.technology.Technology;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String company;

    private String location;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "employment_type")
    private String employmentType;

    @Column(name = "experience_level")
    private String experienceLevel;

    @Column(name = "remote_type")
    private String remoteType;

    @Column(name = "salary_min")
    private BigDecimal salaryMin;

    @Column(name = "salary_max")
    private BigDecimal salaryMax;

    @Column(name = "posted_at")
    private LocalDateTime postedAt;

    @Column(nullable = false)
    private String source;

    @Column(name = "source_url", nullable = false)
    private String sourceUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(
        name = "job_technologies",
        joinColumns = @JoinColumn(name = "job_id"),
        inverseJoinColumns = @JoinColumn(name = "technology_id")
    )
    @Builder.Default
    private Set<Technology> technologies = new HashSet<>();
}