package com.canik.jmi.ingestion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record JobImportRecord(
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
    String sourceUrl
) {
}
