package com.ai.PathFinder.strategy.kruskal;

import java.util.HashMap;
import java.util.Map;

public class UnionFind {

    private Map<String, String> parent = new HashMap<>();

    public void makeSet(String id) {
        parent.put(id, id);
    }

    public String find(String id) {

        if (parent.get(id).equals(id)) {
            return id;
        }

        String root = find(parent.get(id));
        parent.put(id, root);
        return root;
    }

    public void union(String id1, String id2) {
        String root1 = find(id1);
        String root2 = find(id2);

        if (!root1.equals(root2)) {
            parent.put(root1, root2);
        }
    }

}
