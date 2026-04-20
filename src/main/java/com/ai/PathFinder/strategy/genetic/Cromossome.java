package com.ai.PathFinder.strategy.genetic;

import com.ai.PathFinder.strategy.graph.Edge;

import java.util.Set;

public class Cromossome {

    private Set<Edge> ferrovias;
    private double fitness;

    public Cromossome(Set<Edge> ferrovias) {
        this.ferrovias = ferrovias;
    }

    public Set<Edge> getFerrovias() {
        return ferrovias;
    }

    public void setFerrovias(Set<Edge> ferrovias) {
        this.ferrovias = ferrovias;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

}
