package com.ai.PathFinder.strategy.genetic;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.ai.PathFinder.strategy.graph.Edge;
import com.ai.PathFinder.strategy.graph.Node;
import com.ai.PathFinder.strategy.search.AStar;

import java.util.*;

class FitnessEvaluatorTest {

    @Test
    void shouldReturnHighPenaltyWhenOverBudget() {
        AStar aStarMock = mock(AStar.class);
        List<Demand> demands = List.of(new Demand(new Node("A", 0, 0), new Node("B", 0, 0), 10));
        double budget = 1000.0; // Orçamento baixo

        FitnessEvaluator evaluator = new FitnessEvaluator(aStarMock, demands, budget);

        // Ferrovia de 10km custa 20 milhões (10 * 2.000.000)
        Edge expensiveEdge = new Edge(null, null, 10.0, true);
        Cromossome c = new Cromossome(Set.of(expensiveEdge));

        double fitness = evaluator.evaluate(c);

        // Deve retornar a penalidade: (20.000.000 - 1.000) * 10
        assertTrue(fitness > 100_000_000);
        // O A* nem deve ser chamado para economizar tempo
        verify(aStarMock, never()).findRoute(anyString(), anyString(), anySet());
    }
}