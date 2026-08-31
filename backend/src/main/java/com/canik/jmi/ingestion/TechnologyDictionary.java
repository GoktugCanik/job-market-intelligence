package com.canik.jmi.ingestion;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dictionary-based technology extractor: scans free-text job descriptions for
 * known technology names/aliases (case-insensitive, whole-word/phrase match)
 * and resolves them to canonical technology names.
 */
@Component
public class TechnologyDictionary {

    // alias (lowercase) -> canonical technology name, matching the "technologies" table.
    private static final Map<String, String> ALIASES = new LinkedHashMap<>();

    static {
        put("Java", "java");
        put("Python", "python");
        put("TypeScript", "typescript");
        put("Spring Boot", "spring boot", "spring-boot", "springboot");
        put("React", "react", "react.js", "reactjs");
        put("FastAPI", "fastapi", "fast api");
        put("PostgreSQL", "postgresql", "postgres", "postgre sql", "postgres sql");
        put("Docker", "docker");
        put("Git", "git");
        put("HTML", "html");
        put("CSS", "css");
        put("REST APIs", "rest api", "rest apis", "restful api", "restful apis");
    }

    private static void put(String canonical, String... aliases) {
        for (String alias : aliases) {
            ALIASES.put(alias, canonical);
        }
    }

    /**
     * Extracts the set of canonical technology names mentioned in the given text.
     * Case-insensitive, whole-word/phrase matching; each technology is returned
     * at most once even if mentioned multiple times.
     */
    public Set<String> extract(String text) {
        Set<String> found = new LinkedHashSet<>();
        if (text == null || text.isBlank()) {
            return found;
        }
        String haystack = text.toLowerCase(Locale.ROOT);
        for (Map.Entry<String, String> entry : ALIASES.entrySet()) {
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(entry.getKey()) + "\\b");
            Matcher matcher = pattern.matcher(haystack);
            if (matcher.find()) {
                found.add(entry.getValue());
            }
        }
        return found;
    }
}
