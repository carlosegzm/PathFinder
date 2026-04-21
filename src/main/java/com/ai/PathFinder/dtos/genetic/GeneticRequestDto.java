package com.ai.PathFinder.dtos.genetic;

import java.math.BigDecimal;

public record GeneticRequestDto(
    BigDecimal budgetLimit,
    int popSize,
    int generations,
    double mutationRate,
    int tournamentSize
) {}