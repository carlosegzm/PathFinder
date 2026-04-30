package com.ai.PathFinder.strategy.genetic;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ai.PathFinder.strategy.graph.Edge;
import com.ai.PathFinder.strategy.graph.Node;
import com.ai.PathFinder.strategy.search.AStar;

class FitnessEvaluatorTest {

    @Test
    void shouldReturnHighPenaltyWhenOverBudget() {
        AStar aStarMock = mock(AStar.class);

        Node nodeA = new Node("A", 0, 0);
        Node nodeB = new Node("B", 0, 0);

        List<Demand> demands = List.of(
                new Demand(nodeA, nodeB, 10)
        );

        double budget = 1000.0;

        Edge expensiveEdge = new Edge(nodeA, nodeB, 10.0, true);

        List<Edge> allEdges = List.of(expensiveEdge);

        FitnessEvaluator evaluator = new FitnessEvaluator(
                aStarMock,
                demands,
                budget,
                allEdges
        );

        Cromossome c = new Cromossome(Set.of(expensiveEdge));

        double fitness = evaluator.evaluate(c);

        assertTrue(fitness > 100_000_000);

        verify(aStarMock, never()).findRoute(anyString(), anyString(), anySet());
    }
}