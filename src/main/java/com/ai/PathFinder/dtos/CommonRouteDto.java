package com.ai.PathFinder.dtos;

import com.ai.PathFinder.entities.Capital;

public record CommonRouteDto(Long id,
                             Capital origin,
                             Capital destination,
                             Integer load) { }