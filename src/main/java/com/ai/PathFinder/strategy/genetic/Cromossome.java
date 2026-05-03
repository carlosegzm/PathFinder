package com.ai.PathFinder.strategy.genetic;

import com.ai.PathFinder.strategy.graph.Edge;

import java.util.Set;

/**
 * Representa um indivíduo dentro da população do algoritmo genético.
 * 
 *      Esta classe atua como um contêiner para o material genético (o conjunto de ferrovias 
 * construídas) e armazena os resultados de sua avaliação, incluindo o custo total de 
 * transporte para atender as demandas, o custo financeiro de construção das linhas 
 * e o valor de fitness final calculado.
 */
public class Cromossome {

    private Set<Edge> ferrovias;
    private double fitness;
    private double totalTransportCost;
    private double constructionCost;

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

    public double getTotalTransportCost() {
        return totalTransportCost;
    }

    public void setTotalTransportCost(double totalTransportCost) {
        this.totalTransportCost = totalTransportCost;
    }

    public double getConstructionCost() {
        return constructionCost;
    }

    public void setConstructionCost(double constructionCost) {
        this.constructionCost = constructionCost;
    }

}
