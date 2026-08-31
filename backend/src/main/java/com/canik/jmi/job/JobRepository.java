package com.canik.jmi.job;

import com.canik.jmi.analytics.EmploymentTypeCountResponse;
import com.canik.jmi.analytics.ExperienceLevelCountResponse;
import com.canik.jmi.analytics.LocationCountResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("""
        SELECT DISTINCT j FROM Job j
        LEFT JOIN FETCH j.technologies
        WHERE (:location IS NULL OR LOWER(j.location) LIKE LOWER(CAST(:location AS string)))
          AND (:experienceLevel IS NULL OR j.experienceLevel = :experienceLevel)
          AND (:employmentType IS NULL OR j.employmentType = :employmentType)
          AND (:technology IS NULL OR EXISTS (
                SELECT 1 FROM Job j2 JOIN j2.technologies t2
                WHERE j2 = j AND LOWER(t2.name) = LOWER(CAST(:technology AS string))
          ))
        """)
    List<Job> findByFilters(
        @Param("location") String location,
        @Param("technology") String technology,
        @Param("experienceLevel") String experienceLevel,
        @Param("employmentType") String employmentType
    );

    @Query("SELECT j FROM Job j LEFT JOIN FETCH j.technologies WHERE j.id = :id")
    Optional<Job> findByIdWithTechnologies(Long id);

    boolean existsBySourceUrl(String sourceUrl);

    @Query("""
        SELECT new com.canik.jmi.analytics.LocationCountResponse(j.location, COUNT(j))
        FROM Job j GROUP BY j.location ORDER BY COUNT(j) DESC
        """)
    List<LocationCountResponse> countJobsByLocation();

    @Query("""
        SELECT new com.canik.jmi.analytics.ExperienceLevelCountResponse(j.experienceLevel, COUNT(j))
        FROM Job j GROUP BY j.experienceLevel ORDER BY COUNT(j) DESC
        """)
    List<ExperienceLevelCountResponse> countJobsByExperienceLevel();

    @Query("""
        SELECT new com.canik.jmi.analytics.EmploymentTypeCountResponse(j.employmentType, COUNT(j))
        FROM Job j GROUP BY j.employmentType ORDER BY COUNT(j) DESC
        """)
    List<EmploymentTypeCountResponse> countJobsByEmploymentType();
}