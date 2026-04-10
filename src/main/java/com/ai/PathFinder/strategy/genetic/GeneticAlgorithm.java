package com.ai.PathFinder.strategy.genetic;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.ai.PathFinder.strategy.graph.Edge;


// Backbone
public class GeneticAlgorithm {

    private List<Edge> allEdges;
    private FitnessEvaluator evaluator;
    private Random random = new Random();

    public Cromossome gerarIndividuoAleatorio() {
        Set<Edge> ferrovias = new HashSet<>();

        for (Edge e : allEdges) {
            if (random.nextBoolean()) {
                ferrovias.add(e);
            }
        }

        return new Cromossome(ferrovias);
    }
}
