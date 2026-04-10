package com.ai.PathFinder.strategy.genetic;

import com.ai.PathFinder.strategy.graph.Node;

// Rotas mais comuns
public class Demand {
    Node origin;
    Node destiny;
    int quantity;

    public Demand(Node origin, Node destiny, int quantity) {
        this.origin = origin;
        this.destiny = destiny;
        this.quantity = quantity;
    }
}
