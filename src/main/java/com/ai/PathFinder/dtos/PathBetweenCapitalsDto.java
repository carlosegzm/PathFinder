package com.ai.PathFinder.dtos;

import com.ai.PathFinder.entities.Capital;

public record PathBetweenCapitalsDto(Long id,
                                     Capital origin,
                                     Capital destination,
                                     Integer distance) { }