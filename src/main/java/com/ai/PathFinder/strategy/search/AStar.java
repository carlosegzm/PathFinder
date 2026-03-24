package com.ai.PathFinder.strategy.search;

import java.util.List;

import com.ai.PathFinder.entities.Capital;
import com.ai.PathFinder.entities.PathBetweenCapitals;

public interface AStar {

    List<PathBetweenCapitals> findShortestPath(Capital origin, Capital destination);
    
}
