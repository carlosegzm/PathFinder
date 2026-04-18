package com.ai.PathFinder.strategy.search;

import com.ai.PathFinder.entities.Capital;
import com.ai.PathFinder.entities.PathBetweenCapitals;
import com.ai.PathFinder.repositories.CapitalRepository;
import com.ai.PathFinder.repositories.PathBetweenCapitalsRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Algoritmo A* Multimodal.
 *
 * Encontra a rota de menor CUSTO FINANCEIRO (em R$) entre duas capitais,
 * levando em conta rodovias e ferrovias ao mesmo tempo.
 *
 * Custos:
 * - Rodovia: R$ 5,00 por km
 * - Ferrovia: R$ 1,20 por km
 * - Transbordo (troca de caminhão para trem ou vice-versa): + R$ 1.000,00
 *
 * O grafo é carregado uma única vez na memória ao iniciar a aplicação.
 * Durante a busca, nenhuma consulta ao banco é feita.
 */
@Service
@RequiredArgsConstructor
@DependsOn("flyway")
public class AStar {

    // Custos por km segundo o modal
    private static final double ROAD_COST_PER_KM = 5.00;
    private static final double RAILWAY_COST_PER_KM = 1.20;

    // Penalidade ao trocar de modal (caminhão → trem ou trem → caminhão)
    private static final double TRANSFER_PENALTY = 1_000.00;

    // Raio da Terra em km — usado na fórmula de Haversine
    private static final double EARTH_RADIUS_KM = 6_371.0;

    // Repositórios — usados SOMENTE para carregar o grafo, nunca dentro da busca
    private final CapitalRepository capitalRepository;
    private final PathBetweenCapitalsRepository pathRepository;

    // Grafo em memória: sigla da capital → lista de caminhos de saída
    private Map<String, List<Edge>> graph;

    // Mapa de capitais para acessar latitude/longitude rapidamente
    private Map<String, Capital> capitalMap;

    // Carregamento do grafo (executado UMA VEZ ao iniciar a aplicação)
    // TODO!!!! Separar isso por classes PELO AMOR DE DEUS
    @Autowired
    private Flyway flyway;

    @PostConstruct
    public void init() {
        flyway.migrate(); // força migração
        initGraph();
    }

    /**
     * Lê todas as capitais e caminhos do banco e monta o grafo em memória.
     * A partir daqui, nenhuma query SQL é feita durante o A*.
     */
    public void initGraph() {
        List<Capital> capitals = capitalRepository.findAll();
        List<PathBetweenCapitals> paths = pathRepository.findAll();

        // Monta o mapa de capitais por sigla
        capitalMap = new HashMap<>();
        for (Capital c : capitals) {
            capitalMap.put(c.getId(), c);
        }

        // Inicia o grafo com listas vazias para cada capital
        graph = new HashMap<>();
        for (Capital c : capitals) {
            graph.put(c.getId(), new ArrayList<>());
        }

        // Adiciona as arestas: sempre rodovia; ferrovia só se has_railway = true
        for (PathBetweenCapitals path : paths) {
            String from = path.getOrigin().getId();
            String to = path.getDestination().getId();
            int distance = path.getDistance();
            boolean railway = Boolean.TRUE.equals(path.getHasRailway());

            graph.get(from).add(new Edge(to, distance, TransportMode.ROAD));

            if (railway) {
                graph.get(from).add(new Edge(to, distance, TransportMode.RAILWAY));
            }
        }
    }

    /**
     * Reconstrói o grafo com um conjunto de ferrovias específico.
     * Usado pelo Algoritmo Genético para testar diferentes combinações de ferrovias
     * sem precisar alterar o banco de dados.
     *
     * @param railwayEdges conjunto de chaves "SIGLA_ORIGEM-SIGLA_DESTINO" com
     *                     ferrovia ativa
     *                     Exemplo: {"SP-RJ", "RJ-SP", "RJ-MG", "MG-RJ"}
     */
    public void rebuildGraphWithRailways(Set<String> railwayEdges) {
        List<Capital> capitals = capitalRepository.findAll();
        List<PathBetweenCapitals> paths = pathRepository.findAll();

        capitalMap = new HashMap<>();
        for (Capital c : capitals) {
            capitalMap.put(c.getId(), c);
        }

        graph = new HashMap<>();
        for (Capital c : capitals) {
            graph.put(c.getId(), new ArrayList<>());
        }

        for (PathBetweenCapitals path : paths) {
            String from = path.getOrigin().getId();
            String to = path.getDestination().getId();
            int km = path.getDistance();
            String edgeKey = from + "-" + to;

            graph.get(from).add(new Edge(to, km, TransportMode.ROAD));

            if (railwayEdges.contains(edgeKey)) {
                graph.get(from).add(new Edge(to, km, TransportMode.RAILWAY));
            }
        }
    }

    // Busca A*

    /**
     * Encontra a rota de menor custo financeiro entre duas capitais.
     *
     * @param originId      sigla da capital de origem (ex: "SP")
     * @param destinationId sigla da capital de destino (ex: "RJ")
     * @return AStarResult com o custo e a rota, ou AStarResult.empty() se não
     *         houver caminho
     */
    public AStarResult findRoute(String originId, String destinationId, Set<String> activeRailways) {

        // Capitais inexistentes no grafo
        if (!graph.containsKey(originId) || !graph.containsKey(destinationId)) {
            return AStarResult.empty();
        }

        // Origem igual ao destino: custo zero
        if (originId.equals(destinationId)) {
            return new AStarResult(0.0, List.of(), List.of(capitalMap.get(originId)));
        }

        Capital destination = capitalMap.get(destinationId);

        // Fila de prioridade: sempre processa o estado com menor f(n) = g(n) + h(n)
        PriorityQueue<SearchState> openSet = new PriorityQueue<>(
                Comparator.comparingDouble(s -> s.gCost + s.hCost));

        // Registra o menor custo já encontrado para cada par (capital, modal de
        // chegada)
        // A chave inclui o modal porque chegar de trem tem implicação diferente de
        // chegar de caminhão
        Map<String, Double> bestCost = new HashMap<>();

        // Estado inicial: estamos na origem, sem modal anterior (NONE)
        SearchState initial = new SearchState(
                originId,
                null, // estado anterior
                null, // aresta usada
                0.0, // g(n) = 0
                haversineFinancial(capitalMap.get(originId), destination),
                TransportMode.NONE // ainda não usamos nenhum modal
        );

        openSet.add(initial);
        bestCost.put(stateKey(originId, TransportMode.NONE), 0.0);

        while (!openSet.isEmpty()) {
            SearchState current = openSet.poll();

            // Chegamos ao destino!
            if (current.capitalId.equals(destinationId)) {
                return buildResult(current);
            }

            // Se já encontramos um caminho melhor para este estado, ignoramos este
            String currentKey = stateKey(current.capitalId, current.arrivalMode);
            Double recorded = bestCost.get(currentKey);
            if (recorded != null && current.gCost > recorded + 1e-9) {
                continue;
            }

            // Expande os vizinhos
            for (Edge edge : graph.getOrDefault(current.capitalId, Collections.emptyList())) {

                // NOVA LÓGICA DE FILTRO:
                // Se a aresta for ferroviária mas não estiver no conjunto de ferrovias
                // ativas deste indivíduo do AG, nós a ignoramos.
                if (activeRailways != null && edge.mode == TransportMode.RAILWAY && !activeRailways.contains(edge.targetId)) {
                    continue;
                }

                // Custo desta aresta segundo o modal
                double edgeCost = (edge.mode == TransportMode.RAILWAY)
                        ? edge.distanceKm * RAILWAY_COST_PER_KM
                        : edge.distanceKm * ROAD_COST_PER_KM;

                // Adiciona penalidade se houve troca de modal
                double transferCost = 0.0;
                if (current.arrivalMode != TransportMode.NONE
                        && current.arrivalMode != edge.mode) {
                    transferCost = TRANSFER_PENALTY;
                }

                double newG = current.gCost + edgeCost + transferCost;

                String neighborKey = stateKey(edge.targetId, edge.mode);
                Double bestG = bestCost.get(neighborKey);

                // Só adiciona à fila se encontramos um caminho melhor
                if (bestG == null || newG < bestG - 1e-9) {
                    bestCost.put(neighborKey, newG);

                    Capital neighbor = capitalMap.get(edge.targetId);
                    double h = haversineFinancial(neighbor, destination);

                    openSet.add(new SearchState(
                            edge.targetId,
                            current,
                            edge,
                            newG,
                            h,
                            edge.mode));
                }
            }
        }

        return AStarResult.empty();
    }

    // Heurística: Haversine × menor custo possível (R$ 1,20/km)

    /**
     * Calcula a estimativa do custo restante até o destino.
     * Usa a distância em linha reta (Haversine) × R$ 1,20 (menor custo do sistema).
     * Como nunca superestima, garante que o A* encontra sempre a solução ótima.
     */
    private double haversineFinancial(Capital from, Capital to) {
        double km = haversineKm(
                from.getLatitude().doubleValue(),
                from.getLongitude().doubleValue(),
                to.getLatitude().doubleValue(),
                to.getLongitude().doubleValue());
        return km * RAILWAY_COST_PER_KM;
    }

    /**
     * Distância em km entre dois pontos geográficos usando a fórmula de Haversine.
     */
    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    // Métodos auxiliares

    /**
     * Chave composta para o mapa de visitados.
     * "SP|ROAD" e "SP|RAILWAY" são estados DIFERENTES:
     * chegar de caminhão vs chegar de trem implica custos distintos no próximo
     * passo.
     */
    private static String stateKey(String capitalId, TransportMode mode) {
        return capitalId + "|" + mode.name();
    }

    /** Reconstrói a rota completa navegando de trás para frente pelos estados. */
    private AStarResult buildResult(SearchState finalState) {
        LinkedList<Edge> edges = new LinkedList<>();
        LinkedList<Capital> route = new LinkedList<>();

        SearchState current = finalState;
        while (current != null) {
            route.addFirst(capitalMap.get(current.capitalId));
            if (current.edgeUsed != null) {
                edges.addFirst(current.edgeUsed);
            }
            current = current.previous;
        }

        return new AStarResult(finalState.gCost, new ArrayList<>(edges), new ArrayList<>(route));
    }

    // Classes internas

    /**
     * Modal de transporte. NONE é usado apenas no estado inicial (sem modal
     * anterior).
     */
    public enum TransportMode {
        ROAD, // Caminhão — R$ 5,00/km
        RAILWAY, // Trem — R$ 1,20/km
        NONE // Sentinela: estado inicial, sem modal anterior
    }

    /**
     * Representa um trecho do grafo (uma aresta).
     * Entre duas capitais vizinhas pode existir até 2 arestas:
     * uma de rodovia e uma de ferrovia (quando has_railway = true).
     */
    public static class Edge {
        public final String targetId; // sigla da capital de destino
        public final int distanceKm; // distância em km
        public final TransportMode mode; // tipo de transporte

        public Edge(String targetId, int distanceKm, TransportMode mode) {
            this.targetId = targetId;
            this.distanceKm = distanceKm;
            this.mode = mode;
        }
    }

    /**
     * Estado de busca armazenado na fila de prioridade.
     *
     * Guardamos o modal de chegada (arrivalMode) porque precisamos saber
     * se o próximo trecho vai gerar penalidade de transbordo.
     * Sem isso, o algoritmo calcularia os custos de forma incorreta.
     */
    public static class SearchState {
        public final String capitalId; // onde estamos agora
        public final SearchState previous; // de onde viemos (para reconstruir a rota)
        public final Edge edgeUsed; // aresta que usamos para chegar aqui
        public final double gCost; // custo acumulado real em R$
        public final double hCost; // estimativa do custo restante em R$
        public final TransportMode arrivalMode; // como chegamos aqui (ROAD, RAILWAY ou NONE)

        public SearchState(String capitalId, SearchState previous, Edge edgeUsed,
                           double gCost, double hCost, TransportMode arrivalMode) {
            this.capitalId = capitalId;
            this.previous = previous;
            this.edgeUsed = edgeUsed;
            this.gCost = gCost;
            this.hCost = hCost;
            this.arrivalMode = arrivalMode;
        }
    }

    /**
     * Resultado final do A*.
     * Contém o custo total em R$, a lista de trechos usados e a lista de capitais
     * da rota.
     */
    public static class AStarResult {
        public final double totalCostBrl; // custo total da rota em R$
        public final List<Edge> edges; // trechos percorridos (em ordem)
        public final List<Capital> route; // capitais visitadas (em ordem)
        public final boolean found; // true se existe rota

        /** Construtor para quando a rota foi encontrada. */
        public AStarResult(double totalCostBrl, List<Edge> edges, List<Capital> route) {
            this.totalCostBrl = totalCostBrl;
            this.edges = edges;
            this.route = route;
            this.found = true;
        }

        /** Construtor privado para resultado vazio. */
        private AStarResult() {
            this.totalCostBrl = 0.0;
            this.edges = Collections.emptyList();
            this.route = Collections.emptyList();
            this.found = false;
        }

        /** Use este método quando não houver rota disponível. */
        public static AStarResult empty() {
            return new AStarResult();
        }
    }
}