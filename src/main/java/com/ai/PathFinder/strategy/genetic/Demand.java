package com.ai.PathFinder.strategy.genetic;

import com.ai.PathFinder.strategy.graph.Node;

// Rotas mais comuns
public class Demand {
    private Node origin;
    private Node destiny;
    private int quantity;

    public Demand(Node origin, Node destiny, int quantity) {
        this.origin = origin;
        this.destiny = destiny;
        this.quantity = quantity;
    }

    public Node getOrigin() {
        return origin;
    }

    public void setOrigin(Node origin) {
        this.origin = origin;
    }

    public Node getDestiny() {
        return destiny;
    }

    public void setDestiny(Node destiny) {
        this.destiny = destiny;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
