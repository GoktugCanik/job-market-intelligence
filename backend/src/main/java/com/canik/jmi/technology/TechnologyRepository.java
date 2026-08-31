package com.canik.jmi.technology;

import com.canik.jmi.analytics.TechnologyCountResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TechnologyRepository extends JpaRepository<Technology, Long> {

    Optional<Technology> findByNameIgnoreCase(String name);

    @Query("""
        SELECT new com.canik.jmi.analytics.TechnologyCountResponse(t.name, COUNT(j))
        FROM Technology t JOIN t.jobs j GROUP BY t.name ORDER BY COUNT(j) DESC
        """)
    List<TechnologyCountResponse> countJobsByTechnology();
}
