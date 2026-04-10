package com.ai.PathFinder.strategy.genetic;

import com.ai.PathFinder.strategy.graph.Edge;

import java.util.Set;

public class Cromossome {

    Set<Edge> ferrovias;
    double fitness;

    public Cromossome(Set<Edge> ferrovias) {
        this.ferrovias = ferrovias;
    }
}
