package com.ai.PathFinder.dtos;

public record CommonRouteDto(Long id,
                             String origin,
                             String destination,
                             Integer load) { }