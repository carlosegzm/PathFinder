package com.ai.PathFinder.strategy.genetic;

import java.util.List;
import java.util.Set;

import com.ai.PathFinder.strategy.graph.Edge;
import com.ai.PathFinder.strategy.search.RouteCostCalculator;

public class FitnessEvaluator {

    private RouteCostCalculator calculator;
    private List<Demand> demands;
    private double budgetLimit;

    public FitnessEvaluator(RouteCostCalculator calculator,
                            List<Demand> demands,
                            double budgetLimit) {
        this.calculator = calculator;
        this.demands = demands;
        this.budgetLimit = budgetLimit;
    }

    public double evaluate(Cromossome cromossome) {

        double constructionCost = calculateConstructionConst(cromossome.ferrovias);

        if (constructionCost > budgetLimit) {
            return Double.MAX_VALUE; // penalização
        }

        double totalCost = 0;

        for (Demand d : demands) {
            double cost = calculator.calculateCost(
                d.origin, d.destiny, cromossome.ferrovias
            );

            totalCost += cost * d.quantity;
        }

        return totalCost;
    }

    private double calculateConstructionConst(Set<Edge> ferrovias) {
        double total = 0;

        for (Edge e : ferrovias) {
            total +=  e.getDistance() * 2_000_000;
        }

        return total;
    }
}
