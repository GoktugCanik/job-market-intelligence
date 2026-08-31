package com.canik.jmi.ingestion;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Component
public class JobNormalizer {

    private static final Map<String, String> LOCATION_ALIASES = Map.of(
        "istanbul", "Istanbul",
        "ankara", "Ankara",
        "izmir", "Izmir",
        "bursa", "Bursa",
        "antalya", "Antalya"
    );

    private static final Map<String, String> EMPLOYMENT_TYPE_ALIASES = Map.of(
        "fulltime", "FULL_TIME",
        "parttime", "PART_TIME",
        "contract", "CONTRACT",
        "internship", "INTERNSHIP",
        "freelance", "FREELANCE"
    );

    private static final Map<String, String> EXPERIENCE_LEVEL_ALIASES = Map.of(
        "junior", "JUNIOR",
        "entry", "JUNIOR",
        "entrylevel", "JUNIOR",
        "mid", "MID",
        "midlevel", "MID",
        "intermediate", "MID",
        "senior", "SENIOR",
        "lead", "LEAD"
    );

    private static final Map<String, String> REMOTE_TYPE_ALIASES = Map.of(
        "remote", "REMOTE",
        "hybrid", "HYBRID",
        "onsite", "ONSITE",
        "office", "ONSITE"
    );

    public String normalizeLocation(String rawLocation) {
        if (rawLocation == null || rawLocation.isBlank()) {
            return null;
        }
        String key = foldKey(rawLocation);
        String canonical = LOCATION_ALIASES.get(key);
        if (canonical != null) {
            return canonical;
        }
        String trimmed = rawLocation.trim();
        return trimmed.substring(0, 1).toUpperCase(Locale.ROOT) + trimmed.substring(1);
    }

    public String normalizeEmploymentType(String rawEmploymentType) {
        return lookupOrUppercase(rawEmploymentType, EMPLOYMENT_TYPE_ALIASES);
    }

    public String normalizeExperienceLevel(String rawExperienceLevel) {
        return lookupOrUppercase(rawExperienceLevel, EXPERIENCE_LEVEL_ALIASES);
    }

    public String normalizeRemoteType(String rawRemoteType) {
        return lookupOrUppercase(rawRemoteType, REMOTE_TYPE_ALIASES);
    }

    private String lookupOrUppercase(String raw, Map<String, String> aliases) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String canonical = aliases.get(foldKey(raw));
        if (canonical != null) {
            return canonical;
        }
        return raw.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
    }

    /**
     * Folds Turkish dotted/dotless "I" variants to ASCII before lowercasing, so
     * "İstanbul", "Istanbul" and "istanbul" all fold to the same lookup key.
     */
    private String foldKey(String raw) {
        return raw.trim()
            .replace('İ', 'I')
            .replace('ı', 'i')
            .toLowerCase(Locale.ROOT)
            .replace("-", "")
            .replace("_", "")
            .replace(" ", "");
    }
}
