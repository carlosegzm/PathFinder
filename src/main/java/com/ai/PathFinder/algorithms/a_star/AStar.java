package com.ai.PathFinder.algorithms.a_star;

import java.util.List;

import com.ai.PathFinder.entities.Capital;
import com.ai.PathFinder.entities.PathBetweenCapitals;

public interface AStar {

    List<PathBetweenCapitals> findShortestPath(Capital origin, Capital destination);
    
}
