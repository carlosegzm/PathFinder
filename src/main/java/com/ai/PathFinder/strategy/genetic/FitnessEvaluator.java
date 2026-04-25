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

    // O fitness é avaliado com base na malha ferroviária aleatória do cromosomo
    // e como ela influencia na distância entre as capitais das rotas mais comuns
    public double evaluate(Cromossome cromossome) {

        double constructionCost = calculateConstructionConst(cromossome.getFerrovias());

        // penalty
        if (constructionCost > budgetLimit) {
            double penalty = Math.pow((constructionCost - budgetLimit), 3);
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

            // (OTIMIZAÇÃO)
            /**
             * Nota sobre a mudança:
             * O AStar é um service (que é um singleton no spring), e se eu rodar o GA em
             * paralelo ou
             * múltiplas req chegaram simultaneamente, um cromossomo pode sobrescrever o
             * grafo do outro
             * enquanto o A* ainda está rodando.
             * Por isso, a solução é permitir que o A* receba as ferrovias válidas como
             * parâmetro de
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

    double calculateConstructionConst(Set<Edge> ferrovias) {
        double total = 0;
        Set<String> paidPaths = new HashSet<>();

        for (Edge e : ferrovias) {
            String id1 = e.getFrom().getId();
            String id2 = e.getTo().getId();

            // Cria uma chave única determinística: a menor sigla sempre vem primeiro (ordem
            // alfabetica)
            // Exemplo: tanto SP-RJ quanto RJ-SP viram "RJ-SP"
            // Assim podemos tratar A-B como B-A (aresta sem direção)
            String normalizedKey = (id1.compareTo(id2) < 0) ? id1 + "-" + id2 : id2 + "-" + id1;

            if (!paidPaths.contains(normalizedKey)) {
                total += e.getDistance() * 2_000_000;
                paidPaths.add(normalizedKey);
            }
        }
        return total;
    }

    // returns true if the cromossome's construction cost is lower than the budgetlimit
    boolean validConstructionCost(Cromossome c) {
        return calculateConstructionConst(c.getFerrovias()) < budgetLimit;
    }

    private Set<String> buildRailwayKeys(Set<Edge> ferrovias) {
        Set<String> keys = new HashSet<>();
        for (Edge e : ferrovias) {
            String id1 = e.getFrom().getId();
            String id2 = e.getTo().getId();

            // Adiciona o sentido original (ex: A-B)
            keys.add(id1 + "-" + id2);

            // Adiciona o sentido oposto para implementar a aresta bidirecional (ex: B-A)
            // Assim o A* entende que o trecho está ativo nos dois sentidos
            keys.add(id2 + "-" + id1);
        }
        return keys;
    }

}
