package com.ai.PathFinder.strategy.graph;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class Graph {

    Map<Node, List<Edge>> adj = new HashMap<>();

    public void addEdge(Node from, Node to, double distance, boolean hasRailway) {
        adj.computeIfAbsent(from, k -> new ArrayList<>())
                .add(new Edge(from, to, distance, hasRailway));
    }

}
