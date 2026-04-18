package com.ai.PathFinder.strategy.graph;

public class Edge {

    private Node from;
    private Node to;
    private double distance;
    private boolean hasRailway;

    public Edge(Node from, Node to, double distance, boolean hasRailway) {
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.hasRailway = hasRailway;
    }

    public Node getFrom() {
        return from;
    }

    public void setFrom(Node from) {
        this.from = from;
    }

    public Node getTo() {
        return to;
    }

    public void setTo(Node to) {
        this.to = to;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean isHasRailway() {
        return hasRailway;
    }

    public void setHasRailway(boolean hasRailway) {
        this.hasRailway = hasRailway;
    }

}