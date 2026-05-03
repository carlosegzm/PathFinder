package com.ai.PathFinder.strategy.graph;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Estrutura de dados principal que representa a malha de transporte.
 * Utiliza uma lista de adjacência (mapeando cada {@link Node} às suas respectivas {@link Edge}) 
 * para permitir a exploração e navegação entre os diferentes pontos do sistema.
 */
public class Graph {

    Map<Node, List<Edge>> adj = new HashMap<>();

    public void addEdge(Node from, Node to, double distance, boolean hasRailway) {
        adj.computeIfAbsent(from, k -> new ArrayList<>())
                .add(new Edge(from, to, distance, hasRailway));
    }

}
