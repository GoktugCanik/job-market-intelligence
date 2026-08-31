package com.canik.jmi.analytics;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Cross-entity aggregation queries that don't map cleanly onto a single
 * entity's JpaRepository (date bucketing, technology co-occurrence pairs).
 */
@Repository
public class AnalyticsRepository {

    private final EntityManager entityManager;

    public AnalyticsRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> jobsOverTime(String granularity) {
        return entityManager.createNativeQuery("""
            SELECT to_char(date_trunc(:granularity, posted_at), 'YYYY-MM-DD') AS period, COUNT(*) AS cnt
            FROM jobs
            WHERE posted_at IS NOT NULL
            GROUP BY period
            ORDER BY period
            """)
            .setParameter("granularity", granularity)
            .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> technologyCombinations() {
        return entityManager.createNativeQuery("""
            SELECT t1.name, t2.name, COUNT(*) AS cnt
            FROM job_technologies jt1
            JOIN job_technologies jt2 ON jt1.job_id = jt2.job_id AND jt1.technology_id < jt2.technology_id
            JOIN technologies t1 ON t1.id = jt1.technology_id
            JOIN technologies t2 ON t2.id = jt2.technology_id
            GROUP BY t1.name, t2.name
            ORDER BY cnt DESC
            """)
            .getResultList();
    }
}
