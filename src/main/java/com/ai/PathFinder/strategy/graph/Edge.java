package com.ai.PathFinder.strategy.graph;

public class Edge {

    Node from;
    Node to;
    double distance;
    boolean hasRailway;

    public Edge(Node from, Node to, double distance, boolean hasRailway) {
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.hasRailway = hasRailway;
    }

}
