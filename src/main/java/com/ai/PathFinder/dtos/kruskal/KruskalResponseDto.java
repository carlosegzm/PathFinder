package com.ai.PathFinder.dtos.kruskal;

import com.ai.PathFinder.entities.PathBetweenCapitals;

import java.math.BigDecimal;
import java.util.Set;

public record KruskalResponseDto(double totalDistanceKm,
                                 BigDecimal totalConstructionCost,
                                 BigDecimal availableBudgetForGenetics,
                                 Set<String> railwayNetwork) { }