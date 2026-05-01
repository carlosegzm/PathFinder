package com.ai.PathFinder.strategy.kruskal;

import java.util.HashMap;
import java.util.Map;

// ajuda a agrupar as capitais e verificar se duas cidades já estão conectadas (evitando a criação de ciclos)
public class UnionFind {

    // mapeia o nó para o seu nó pai
    private Map<String, String> parent = new HashMap<>();

    // cria um novo conjunto onde o elemento é o seu próprio pai
    public void makeSet(String id) {
        parent.put(id, id);
    }

    // retorna o pai do conjunto ao qual o id pertence.
    public String find(String id) {

        // se a capital aponta para ela mesma, ela é o pai
        if (parent.get(id).equals(id)) {
            return id;
        }

        // se não for, procura o pai e já atualiza para ficar mais rápido na próxima
        String root = find(parent.get(id));
        parent.put(id, root);
        return root;
    }

    // junta duas capitais no mesmo grupo
    public void union(String id1, String id2) {
        String root1 = find(id1);
        String root2 = find(id2);

        // só faz a união se elas ainda estiverem em grupos separados
        if (!root1.equals(root2)) {
            parent.put(root1, root2);
        }
    }

}
