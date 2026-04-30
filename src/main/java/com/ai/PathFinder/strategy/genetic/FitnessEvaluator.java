package com.ai.PathFinder.strategy.genetic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import com.ai.PathFinder.strategy.graph.Edge;
import com.ai.PathFinder.strategy.search.AStar;

public class FitnessEvaluator {

    private static final double ROAD_COST_PER_KM = 5.00;
    private static final double RAILWAY_COST_PER_KM = 1.20;
    private static final double TRANSFER_PENALTY = 1_000.00;
    private static final double INVALID_ROUTE_COST = 1e12;
    private static final double INVALID_BUDGET_BASE_PENALTY = 1e18;

    private static final int MODE_ROAD = 0;
    private static final int MODE_RAILWAY = 1;
    private static final int MODE_NONE = 2;
    private static final int MODE_COUNT = 3;

    private final List<Demand> demands;
    private final double budgetLimit;
    private final List<Edge> allEdges;

    private final Map<String, Integer> cityIndex = new HashMap<>();
    private final List<FastEdge>[] adjacency;

    private final Map<String, Double> routeCostCache = new HashMap<>();

    @SuppressWarnings("unchecked")
    public FitnessEvaluator(
            AStar ignoredAStar,
            List<Demand> demands,
            double budgetLimit,
            List<Edge> allEdges
    ) {
        this.demands = demands;
        this.budgetLimit = budgetLimit;
        this.allEdges = allEdges;

        indexCities();

        this.adjacency = new List[cityIndex.size()];
        for (int i = 0; i < adjacency.length; i++) {
            adjacency[i] = new ArrayList<>();
        }

        buildAdjacency();
    }

    public double evaluate(Cromossome cromossome) {
        double constructionCost = calculateConstructionConst(cromossome.getFerrovias());
        cromossome.setConstructionCost(constructionCost);

        if (constructionCost > budgetLimit) {
            double penalty = INVALID_BUDGET_BASE_PENALTY + Math.pow(constructionCost - budgetLimit, 2);
            cromossome.setTotalTransportCost(penalty);
            return penalty;
        }

        Set<String> railwayKeys = buildRailwayKeys(cromossome.getFerrovias());
        String configurationKey = buildConfigurationKey(cromossome.getFerrovias());

        double totalCost = 0;

        for (Demand d : demands) {
            String originId = d.getOrigin().getId();
            String destinyId = d.getDestiny().getId();

            String cacheKey = originId + ">" + destinyId + "|" + configurationKey;

            Double cachedCost = routeCostCache.get(cacheKey);
            double cost;

            if (cachedCost != null) {
                cost = cachedCost;
            } else {
                cost = findCheapestCost(originId, destinyId, railwayKeys);
                routeCostCache.put(cacheKey, cost);
            }

            totalCost += cost * d.getQuantity();
        }

        cromossome.setTotalTransportCost(totalCost);

        return totalCost + (constructionCost * 0.000001);
    }

    double calculateConstructionConst(Set<Edge> ferrovias) {
        double total = 0;
        Set<String> paidPaths = new HashSet<>();

        for (Edge e : ferrovias) {
            String normalizedKey = normalizeEdgeKey(e.getFrom().getId(), e.getTo().getId());

            if (paidPaths.add(normalizedKey)) {
                total += e.getDistance() * 2_000_000;
            }
        }

        return total;
    }

    boolean validConstructionCost(Cromossome c) {
        return calculateConstructionConst(c.getFerrovias()) <= budgetLimit;
    }

    private void indexCities() {
        for (Edge edge : allEdges) {
            addCity(edge.getFrom().getId());
            addCity(edge.getTo().getId());
        }

        for (Demand demand : demands) {
            addCity(demand.getOrigin().getId());
            addCity(demand.getDestiny().getId());
        }
    }

    private void addCity(String id) {
        cityIndex.computeIfAbsent(id, ignored -> cityIndex.size());
    }

    private void buildAdjacency() {
        for (Edge edge : allEdges) {
            int from = cityIndex.get(edge.getFrom().getId());
            int to = cityIndex.get(edge.getTo().getId());

            adjacency[from].add(new FastEdge(
                    to,
                    edge.getDistance(),
                    edge.getFrom().getId() + "-" + edge.getTo().getId()
            ));
        }
    }

    private double findCheapestCost(String originId, String destinationId, Set<String> activeRailways) {
        Integer origin = cityIndex.get(originId);
        Integer destination = cityIndex.get(destinationId);

        if (origin == null || destination == null) {
            return INVALID_ROUTE_COST;
        }

        if (origin.equals(destination)) {
            return 0;
        }

        double[] distances = new double[cityIndex.size() * MODE_COUNT];
        Arrays.fill(distances, Double.POSITIVE_INFINITY);

        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingDouble(s -> s.cost));

        int startState = stateIndex(origin, MODE_NONE);
        distances[startState] = 0;
        queue.add(new State(origin, MODE_NONE, 0));

        while (!queue.isEmpty()) {
            State current = queue.poll();
            int currentStateIndex = stateIndex(current.city, current.mode);

            if (current.cost > distances[currentStateIndex] + 1e-9) {
                continue;
            }

            if (current.city == destination) {
                return current.cost;
            }

            for (FastEdge edge : adjacency[current.city]) {
                relax(queue, distances, current, edge, MODE_ROAD, edge.distanceKm * ROAD_COST_PER_KM);

                if (activeRailways.contains(edge.key)) {
                    relax(queue, distances, current, edge, MODE_RAILWAY, edge.distanceKm * RAILWAY_COST_PER_KM);
                }
            }
        }

        return INVALID_ROUTE_COST;
    }

    private void relax(
            PriorityQueue<State> queue,
            double[] distances,
            State current,
            FastEdge edge,
            int nextMode,
            double edgeCost
    ) {
        double transferCost = current.mode != MODE_NONE && current.mode != nextMode
                ? TRANSFER_PENALTY
                : 0;

        double nextCost = current.cost + edgeCost + transferCost;
        int nextStateIndex = stateIndex(edge.to, nextMode);

        if (nextCost < distances[nextStateIndex] - 1e-9) {
            distances[nextStateIndex] = nextCost;
            queue.add(new State(edge.to, nextMode, nextCost));
        }
    }

    private int stateIndex(int city, int mode) {
        return city * MODE_COUNT + mode;
    }

    private Set<String> buildRailwayKeys(Set<Edge> ferrovias) {
        Set<String> keys = new HashSet<>();

        for (Edge e : ferrovias) {
            String id1 = e.getFrom().getId();
            String id2 = e.getTo().getId();

            keys.add(id1 + "-" + id2);
            keys.add(id2 + "-" + id1);
        }

        return keys;
    }

    private String buildConfigurationKey(Set<Edge> ferrovias) {
        List<String> keys = new ArrayList<>(ferrovias.size());

        for (Edge edge : ferrovias) {
            keys.add(normalizeEdgeKey(edge.getFrom().getId(), edge.getTo().getId()));
        }

        Collections.sort(keys);
        return String.join(",", keys);
    }

    private String normalizeEdgeKey(String id1, String id2) {
        return (id1.compareTo(id2) < 0) ? id1 + "-" + id2 : id2 + "-" + id1;
    }

    private static class FastEdge {
        private final int to;
        private final double distanceKm;
        private final String key;

        private FastEdge(int to, double distanceKm, String key) {
            this.to = to;
            this.distanceKm = distanceKm;
            this.key = key;
        }
    }

    private static class State {
        private final int city;
        private final int mode;
        private final double cost;

        private State(int city, int mode, double cost) {
            this.city = city;
            this.mode = mode;
            this.cost = cost;
        }
    }
}