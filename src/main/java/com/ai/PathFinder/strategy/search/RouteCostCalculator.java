package com.ai.PathFinder.strategy.search;

import com.ai.PathFinder.strategy.graph.Node;
import com.ai.PathFinder.strategy.graph.Edge;

import java.util.Set;

// interface pra possibilitar a implementação do genético enquanto não há A*
public interface RouteCostCalculator {
    double calculateCost(Node origem, Node destino, Set<Edge> ferrovias);
}
