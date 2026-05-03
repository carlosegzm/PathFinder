package com.ai.PathFinder.strategy.graph;

import com.ai.PathFinder.entities.Capital;
import com.ai.PathFinder.entities.CommonRoute;
import com.ai.PathFinder.entities.PathBetweenCapitals;
import com.ai.PathFinder.strategy.genetic.Demand;

/**
 * Classe utilitária responsável pela conversão (mapeamento) entre as entidades de persistência 
 * do banco de dados e os objetos de lógica de grafo do sistema.
 * Facilita a transformação de Capitais, Caminhos e Rotas Comuns em {@link Node}, {@link Edge} 
 * e {@link Demand}, respectivamente.
 */
public class Adapter {

    public static Node fromCapital(Capital c) {
        return new Node(
            c.getId(), 
            c.getLatitude().doubleValue(), 
            c.getLongitude().doubleValue()
        );
    }

    public static Edge fromPath(PathBetweenCapitals p) {
        return new Edge(
                fromCapital(p.getOrigin()),
                fromCapital(p.getDestination()),
                p.getDistance(),
                p.getHasRailway()
        );
    }

    public static Demand fromCommonRoute(CommonRoute c){
        return new Demand(
            fromCapital(c.getOrigin()),
            fromCapital(c.getDestination()),
            c.getLoad()
        );
    }
}