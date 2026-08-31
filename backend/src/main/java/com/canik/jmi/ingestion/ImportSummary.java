package com.canik.jmi.ingestion;

public record ImportSummary(
    int totalRecords,
    int imported,
    int skippedExisting,
    int technologiesCreated
) {
}
