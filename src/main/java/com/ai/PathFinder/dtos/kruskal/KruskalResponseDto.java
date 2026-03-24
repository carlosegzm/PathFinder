package com.ai.PathFinder.dtos.kruskal;

import java.math.BigDecimal;

public record KruskalResponseDto(double totalDistanceKm,
                                 BigDecimal totalConstructionCost,
                                 BigDecimal availableBudgetForGenetics) { }