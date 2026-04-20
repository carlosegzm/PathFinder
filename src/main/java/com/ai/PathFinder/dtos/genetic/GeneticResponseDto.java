package com.ai.PathFinder.dtos.genetic;

import java.util.List;

public record GeneticResponseDto(
    double totalTransportCost,
    double constructionCost,
    double budgetLimit,
    List<String> selectedRailways // Lista de IDs das arestas. ex: "SP-RJ"
) {}
