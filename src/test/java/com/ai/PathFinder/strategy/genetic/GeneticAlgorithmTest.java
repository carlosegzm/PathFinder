package com.ai.PathFinder.strategy.genetic;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.ai.PathFinder.strategy.graph.Edge;

import java.util.*;

class GeneticAlgorithmTest {

    @Test
    void testCrossoverIntegrity() {
        Edge e1 = new Edge(null, null, 10, true);
        Edge e2 = new Edge(null, null, 20, true);
        
        GeneticAlgorithm ga = new GeneticAlgorithm(List.of(e1, e2), null);
        
        Cromossome p1 = new Cromossome(Set.of(e1));
        Cromossome p2 = new Cromossome(Set.of(e2));
        
        Cromossome child = ga.crossover(p1, p2);
        
        // O filho não pode ter mutações ou arestas mágicas aqui, apenas o que veio dos pais
        for(Edge e : child.getFerrovias()) {
            assertTrue(e.equals(e1) || e.equals(e2));
        }
    }

    @Test
    void testMutationChange() {
        Edge e1 = new Edge(null, null, 10, true);
        GeneticAlgorithm ga = new GeneticAlgorithm(List.of(e1), null);
        Cromossome c = new Cromossome(new HashSet<>()); // Começa vazio
        
        // Força mutação com taxa 100%
        ga.mutate(c, 1.0);
        
        // Se era vazio e a única opção é e1, agora deve conter e1
        assertFalse(c.getFerrovias().isEmpty());
    }
}