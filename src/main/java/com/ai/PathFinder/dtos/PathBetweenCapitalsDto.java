package com.ai.PathFinder.dtos;

public record PathBetweenCapitalsDto(Long id,
                                     String origin,
                                     String destination,
                                     Integer distance) { }