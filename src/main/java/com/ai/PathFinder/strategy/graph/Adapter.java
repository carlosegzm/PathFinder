package com.ai.PathFinder.strategy.graph;

import com.ai.PathFinder.entities.Capital;
import com.ai.PathFinder.entities.PathBetweenCapitals;

public class Adapter {

    Node fromCapital(Capital c) {
        return new Node(c.getId(), c.getLatitude().doubleValue(), c.getLongitude().doubleValue());
    }

    Edge fromPath(PathBetweenCapitals p) {
        return new Edge(
                fromCapital(p.getOrigin()),
                fromCapital(p.getDestination()),
                p.getDistance(),
                p.getHasRailway());
    }
}
