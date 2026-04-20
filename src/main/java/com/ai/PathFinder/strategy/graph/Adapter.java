package com.ai.PathFinder.strategy.graph;

import com.ai.PathFinder.entities.Capital;
import com.ai.PathFinder.entities.CommonRoute;
import com.ai.PathFinder.entities.PathBetweenCapitals;
import com.ai.PathFinder.strategy.genetic.Demand;

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