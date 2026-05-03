package com.ai.PathFinder.strategy.kruskal;

import java.util.HashMap;
import java.util.Map;

// ajuda a agrupar as capitais e verificar se duas cidades já estão conectadas (evitando a criação de ciclos)
public class UnionFind {

    // mapeia o nó para o seu nó pai
    private Map<String, String> parent = new HashMap<>();

    /**
     * Cria um novo conjunto para uma capital específica, onde ela é inicialmente
     * seu próprio pai (representante).
     *
     * @param id O identificador único da capital.
     */
    public void makeSet(String id) {
        parent.put(id, id);
    }

    /**
     * Encontra o representante (raiz) do conjunto ao qual o identificador pertence.
     * Implementa a técnica de compactação de caminho (path compression) para
     * otimizar buscas futuras, atualizando o pai diretamente para a raiz.
     * 
     * @param id O identificador da capital a ser buscada.
     * 
     * @return O identificador da capital raiz do conjunto.
     */
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

    /**
     * Une dois conjuntos distintos em um único grupo.
     * Verifica se os elementos já pertencem ao mesmo grupo antes de realizar a
     * operação para evitar redundâncias.
     * 
     * @param id1 Identificador da primeira capital.
     * @param id2 Identificador da segunda capital.
     */
    public void union(String id1, String id2) {
        String root1 = find(id1);
        String root2 = find(id2);

        // só faz a união se elas ainda estiverem em grupos separados
        if (!root1.equals(root2)) {
            parent.put(root1, root2);
        }
    }

}
