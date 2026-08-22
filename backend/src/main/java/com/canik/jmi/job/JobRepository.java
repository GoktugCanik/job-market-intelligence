package com.canik.jmi.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("SELECT DISTINCT j FROM Job j LEFT JOIN FETCH j.technologies")
    List<Job> findAllWithTechnologies();

    @Query("SELECT j FROM Job j LEFT JOIN FETCH j.technologies WHERE j.id = :id")
    Optional<Job> findByIdWithTechnologies(Long id);
}