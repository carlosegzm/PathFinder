package com.ai.PathFinder.strategy.genetic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import com.ai.PathFinder.strategy.graph.Edge;

public class FitnessEvaluator {

    private static final double ROAD_COST_PER_KM = 5.00;
    private static final double RAILWAY_COST_PER_KM = 1.20;
    private static final double TRANSFER_PENALTY = 1_000.00;
    private static final double INVALID_ROUTE_COST = 1e12;
    private static final double INVALID_BUDGET_BASE_PENALTY = 1e18;

    private static final int MODE_ROAD = 0;
    private static final int MODE_RAILWAY = 1;
    private static final int MODE_NONE = 2;
    private static final int MODE_COUNT = 3;

    private final List<Demand> demands;
    private final double budgetLimit;
    private final List<Edge> allEdges;

    private final Map<String, Integer> cityIndex = new HashMap<>();
    private final List<FastEdge>[] adjacency;

    private final Map<String, Double> routeCostCache = new HashMap<>();

    /**
     * Inicializa o avaliador de fitness, indexando as cidades e construindo a lista
     * de adjacência
     * baseada em todas as arestas possíveis do grafo.
     * 
     * @param demands     Lista de demandas de transporte entre origens e destinos.
     * @param budgetLimit Limite máximo de orçamento para construção de ferrovias.
     * @param allEdges    Lista de todas as conexões (arestas) geográficas
     *                    disponíveis.
     */
    @SuppressWarnings("unchecked")
    public FitnessEvaluator(
            List<Demand> demands,
            double budgetLimit,
            List<Edge> allEdges) {
        this.demands = demands;
        this.budgetLimit = budgetLimit;
        this.allEdges = allEdges;

        indexCities();

        this.adjacency = new List[cityIndex.size()];
        for (int i = 0; i < adjacency.length; i++) {
            adjacency[i] = new ArrayList<>();
        }

        buildAdjacency();
    }

    /**
     * Realiza a avaliação principal de um cromossomo. Calcula o custo de construção
     * das ferrovias
     * e o custo total de transporte para atender a todas as demandas.
     * Se o custo de construção exceder o orçamento, aplica uma penalidade severa.
     *
     * @param cromossome O cromossome (solução) a ser avaliado.
     * @return O valor de fitness (quanto menor, melhor a solução).
     */
    public double evaluate(Cromossome cromossome) {
        double constructionCost = calculateConstructionConst(cromossome.getFerrovias());
        cromossome.setConstructionCost(constructionCost);

        if (constructionCost > budgetLimit) {
            double penalty = INVALID_BUDGET_BASE_PENALTY + Math.pow(constructionCost - budgetLimit, 2);
            cromossome.setTotalTransportCost(penalty);
            return penalty;
        }

        Set<String> railwayKeys = buildRailwayKeys(cromossome.getFerrovias());
        String configurationKey = buildConfigurationKey(cromossome.getFerrovias());

        double totalCost = 0;

        for (Demand d : demands) {
            String originId = d.getOrigin().getId();
            String destinyId = d.getDestiny().getId();

            String cacheKey = originId + ">" + destinyId + "|" + configurationKey;

            Double cachedCost = routeCostCache.get(cacheKey);
            double cost;

            if (cachedCost != null) {
                cost = cachedCost;
            } else {
                cost = findCheapestCost(originId, destinyId, railwayKeys);
                routeCostCache.put(cacheKey, cost);
            }

            totalCost += cost * d.getQuantity();
        }

        cromossome.setTotalTransportCost(totalCost);

        return totalCost + (constructionCost * 0.000001);
    }

    /**
     * Calcula o custo total de construção das ferrovias presentes no conjunto.
     * Garante que caminhos duplicados (em sentidos opostos) não sejam cobrados duas
     * vezes
     * através da normalização de chaves.
     * 
     * @param ferrovias Conjunto de arestas que representam as ferrovias
     *                  construídas.
     * @return O custo total financeiro da construção.
     */
    double calculateConstructionConst(Set<Edge> ferrovias) {
        double total = 0;
        Set<String> paidPaths = new HashSet<>();

        for (Edge e : ferrovias) {
            String normalizedKey = normalizeEdgeKey(e.getFrom().getId(), e.getTo().getId());

            if (paidPaths.add(normalizedKey)) {
                total += e.getDistance() * 2_000_000;
            }
        }

        return total;
    }

    /**
     * Verifica se o custo de construção das ferrovias de um cromossomo está dentro
     * do limite de orçamento definido.
     * 
     * @param c O cromossomo a ser verificado.
     * @return true se estiver dentro do orçamento, false caso contrário.
     */
    boolean validConstructionCost(Cromossome c) {
        return calculateConstructionConst(c.getFerrovias()) <= budgetLimit;
    }

    /**
     * Mapeia todos os IDs de cidades únicos para índices inteiros para otimizar
     * o acesso em arrays e matrizes.
     */
    private void indexCities() {
        for (Edge edge : allEdges) {
            addCity(edge.getFrom().getId());
            addCity(edge.getTo().getId());
        }

        for (Demand demand : demands) {
            addCity(demand.getOrigin().getId());
            addCity(demand.getDestiny().getId());
        }
    }

    /**
     * Adiciona uma cidade ao mapeamento de índices caso ela ainda não esteja
     * presente.
     * Utiliza o tamanho atual do mapa como o próximo índice disponível, garantindo
     * IDs sequenciais.
     *
     * @param id O identificador único da cidade (String).
     */
    private void addCity(String id) {
        cityIndex.computeIfAbsent(id, ignored -> cityIndex.size());
    }

    /**
     * Constrói a estrutura de lista de adjacência interna para uma navegação rápida
     * durante a busca de caminhos.
     */
    private void buildAdjacency() {
        for (Edge edge : allEdges) {
            int from = cityIndex.get(edge.getFrom().getId());
            int to = cityIndex.get(edge.getTo().getId());

            adjacency[from].add(new FastEdge(
                    to,
                    edge.getDistance(),
                    edge.getFrom().getId() + "-" + edge.getTo().getId()));
        }
    }

    /**
     * Encontra o custo de transporte mais barato entre duas cidades utilizando o
     * algoritmo de Dijkstra.
     * O cálculo considera o transporte multimodal (rodoviário e ferroviário) e
     * aplica penalidades
     * para transferências entre modos.
     * 
     * @param originId       Identificador da cidade de origem.
     * @param destinationId  Identificador da cidade de destino.
     * @param activeRailways Conjunto de chaves de ferrovias que foram construídas e
     *                       estão ativas.
     * @return O menor custo de transporte encontrado ou um valor de custo inválido
     *         se não houver rota.
     */
    private double findCheapestCost(String originId, String destinationId, Set<String> activeRailways) {
        Integer origin = cityIndex.get(originId);
        Integer destination = cityIndex.get(destinationId);

        if (origin == null || destination == null) {
            return INVALID_ROUTE_COST;
        }

        if (origin.equals(destination)) {
            return 0;
        }

        double[] distances = new double[cityIndex.size() * MODE_COUNT];
        Arrays.fill(distances, Double.POSITIVE_INFINITY);

        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingDouble(s -> s.cost));

        int startState = stateIndex(origin, MODE_NONE);
        distances[startState] = 0;
        queue.add(new State(origin, MODE_NONE, 0));

        while (!queue.isEmpty()) {
            State current = queue.poll();
            int currentStateIndex = stateIndex(current.city, current.mode);

            if (current.cost > distances[currentStateIndex] + 1e-9) {
                continue;
            }

            if (current.city == destination) {
                return current.cost;
            }

            for (FastEdge edge : adjacency[current.city]) {
                relax(queue, distances, current, edge, MODE_ROAD, edge.distanceKm * ROAD_COST_PER_KM);

                if (activeRailways.contains(edge.key)) {
                    relax(queue, distances, current, edge, MODE_RAILWAY, edge.distanceKm * RAILWAY_COST_PER_KM);
                }
            }
        }

        return INVALID_ROUTE_COST;
    }

    /**
     * Executa a operação de "relaxamento" de uma aresta para o algoritmo de caminho
     * mínimo.
     * Calcula o custo do próximo estado considerando se houve mudança no modo de
     * transporte
     * (aplicando a penalidade de transferência).
     * 
     * @param queue     Fila de prioridade do algoritmo de busca.
     * @param distances Array de distâncias mínimas conhecidas por estado.
     * @param current   Estado atual da busca.
     * @param edge      Aresta sendo explorada.
     * @param nextMode  O modo de transporte da próxima aresta (ROAD ou RAILWAY).
     * @param edgeCost  O custo intrínseco de percorrer a aresta.
     */
    private void relax(
            PriorityQueue<State> queue,
            double[] distances,
            State current,
            FastEdge edge,
            int nextMode,
            double edgeCost) {
        double transferCost = current.mode != MODE_NONE && current.mode != nextMode
                ? TRANSFER_PENALTY
                : 0;

        double nextCost = current.cost + edgeCost + transferCost;
        int nextStateIndex = stateIndex(edge.to, nextMode);

        if (nextCost < distances[nextStateIndex] - 1e-9) {
            distances[nextStateIndex] = nextCost;
            queue.add(new State(edge.to, nextMode, nextCost));
        }
    }

    /**
     * Calcula um índice linear único para representar um estado na busca,
     * combinando a cidade e o modo de transporte.
     * Essencial para o mapeamento em arrays unidimensionais de distâncias.
     * 
     * @param city O índice numérico da cidade.
     * @param mode O modo de transporte atual (ROAD, RAILWAY ou NONE).
     * @return O índice calculado para o estado.
     */
    private int stateIndex(int city, int mode) {
        return city * MODE_COUNT + mode;
    }

    /**
     * Gera um conjunto de chaves de texto para as ferrovias construídas,
     * registrando ambos os sentidos (A-B e B-A) para facilitar a busca rápida.
     * 
     * @param ferrovias Conjunto de arestas ferroviárias.
     * @return Conjunto de strings formatadas como "origem-destino".
     */
    private Set<String> buildRailwayKeys(Set<Edge> ferrovias) {
        Set<String> keys = new HashSet<>();

        for (Edge e : ferrovias) {
            String id1 = e.getFrom().getId();
            String id2 = e.getTo().getId();

            keys.add(id1 + "-" + id2);
            keys.add(id2 + "-" + id1);
        }

        return keys;
    }

    /**
     * Cria uma chave única de configuração que representa o conjunto de ferrovias
     * de forma ordenada. Utilizada para cache de rotas.
     * 
     * @param ferrovias Conjunto de ferrovias construídas.
     * @return Uma string única representando a configuração atual.
     */
    private String buildConfigurationKey(Set<Edge> ferrovias) {
        List<String> keys = new ArrayList<>(ferrovias.size());

        for (Edge edge : ferrovias) {
            keys.add(normalizeEdgeKey(edge.getFrom().getId(), edge.getTo().getId()));
        }

        Collections.sort(keys);
        return String.join(",", keys);
    }

    /**
     * Normaliza a identificação de uma aresta entre dois pontos, garantindo que
     * o ID alfabeticamente menor venha primeiro. Garante que (A,B) e (B,A) sejam
     * tratados como o mesmo caminho.
     * 
     * @param id1 ID da primeira cidade.
     * @param id2 ID da segunda cidade.
     * @return Chave normalizada no formato "menorID-maiorID".
     */
    private String normalizeEdgeKey(String id1, String id2) {
        return (id1.compareTo(id2) < 0) ? id1 + "-" + id2 : id2 + "-" + id1;
    }

    /**
     * Representação otimizada de uma aresta para cálculos de grafo.
     * Armazena o destino, a distância e a chave de identificação para acesso
     * rápido.
     */
    private static class FastEdge {
        private final int to;
        private final double distanceKm;
        private final String key;

        private FastEdge(int to, double distanceKm, String key) {
            this.to = to;
            this.distanceKm = distanceKm;
            this.key = key;
        }
    }

    /**
     * Representa um estado específico durante a exploração do algoritmo de busca.
     * Mantém o registro da cidade atual, o modo de transporte utilizado para chegar
     * nela e o custo acumulado.
     */
    private static class State {
        private final int city;
        private final int mode;
        private final double cost;

        private State(int city, int mode, double cost) {
            this.city = city;
            this.mode = mode;
            this.cost = cost;
        }
    }
}