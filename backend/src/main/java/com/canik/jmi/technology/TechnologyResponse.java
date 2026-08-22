package com.canik.jmi.technology;

public record TechnologyResponse(Long id, String name, String category) {

    public static TechnologyResponse from(Technology technology) {
        return new TechnologyResponse(technology.getId(), technology.getName(), technology.getCategory());
    }
}
