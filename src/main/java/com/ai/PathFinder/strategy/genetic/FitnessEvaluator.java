package com.ai.PathFinder.strategy.genetic;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.ai.PathFinder.strategy.graph.Edge;
import com.ai.PathFinder.strategy.search.AStar;

public class FitnessEvaluator {

    private AStar aStar;
    private List<Demand> demands;
    private double budgetLimit;

    // Optimization: cromossome cache
    private Map<String, Double> cache = new ConcurrentHashMap<>();

    public FitnessEvaluator(AStar aStar,
            List<Demand> demands,
            double budgetLimit) {
        this.aStar = aStar;
        this.demands = demands;
        this.budgetLimit = budgetLimit;
    }

    public double evaluate(Cromossome cromossome) {

        double constructionCost = calculateConstructionConst(cromossome.getFerrovias());
        
        // penalty
        if (constructionCost > budgetLimit) {
            double penalty = (constructionCost - budgetLimit) * 10;
            return penalty;
        }
        
        double totalCost = 0;

        Set<String> railwayKeys = buildRailwayKeys(cromossome.getFerrovias());

        // aStar.rebuildGraphWithRailways(railwayKeys);

        for (Demand d : demands) {
            String originId = d.getOrigin().getId();
            String destinyId = d.getDestiny().getId();

            String key = originId + "-" + destinyId + "-" + railwayKeys.toString();

            // caching
            if (cache.containsKey(key)) {
                totalCost += cache.get(key) * d.getQuantity();
                continue;
            }

            // TODO !!!! Refatorar o A* pra: findRoute(origin, dest, Set<Edge> ferrovias)
            // (OTIMIZAÇÃO)
            /**
             * Nota sobre a mudança: 
             * O AStar é um service (que é um singleton no spring), e se eu rodar o GA em paralelo ou 
             * múltiplas req chegaram simultaneamente, um cromossomo pode sobrescrever o grafo do outro
             * enquanto o A* ainda está rodando.
             * Por isso, a solução é permitir que o A* receba as ferrovias válidas como parâmetro de 
             * busca, ao invés de alterar um estado global da classe
             */
            AStar.AStarResult result = aStar.findRoute(originId, destinyId, railwayKeys);

            double cost;

            // invalid route
            if (!result.found) {
                cost = 1e9;
            } else {
                cost = result.totalCostBrl;
            }

            cache.put(key, cost);

            totalCost += cost * d.getQuantity();
        }

        return totalCost;
    }

    private double calculateConstructionConst(Set<Edge> ferrovias) {
        double total = 0;

        for (Edge e : ferrovias) {
            total += e.getDistance() * 2_000_000;
        }

        return total;
    }

    private Set<String> buildRailwayKeys(Set<Edge> ferrovias) {
        Set<String> keys = new HashSet<>();

        for (Edge e : ferrovias) {
            String from = e.getFrom().getId();
            String to = e.getTo().getId();

            keys.add(from + "-" + to);
        }

        return keys;
    }
}
