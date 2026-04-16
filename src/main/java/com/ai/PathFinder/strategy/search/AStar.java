package com.ai.PathFinder.strategy.search;

import com.ai.PathFinder.entities.Capital;
import com.ai.PathFinder.entities.PathBetweenCapitals;
import com.ai.PathFinder.repositories.CapitalRepository;
import com.ai.PathFinder.repositories.PathBetweenCapitalsRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Algoritmo A* Multimodal.
 *
 * Encontra a rota de menor CUSTO FINANCEIRO (em R$) entre duas capitais,
 * levando em conta rodovias e ferrovias ao mesmo tempo.
 *
 * Custos:
 *   - Rodovia:  R$ 5,00 por km
 *   - Ferrovia: R$ 1,20 por km
 *   - Transbordo (troca de modal): + R$ 1.000,00
 *
 * Otimização para o AG: Boga's dream 😁
 *   O banco de dados é lido uma vez no startup (@PostConstruct).
 *   Os dados brutos ficam em "roadGraph" (só rodovias) e "rawPaths" (lista de arestas base).
 *   Quando o AG chama buildGraphForRailways(), o grafo é remontado APENAS EM MEMÓRIA,
 *   sem nenhuma query SQL — operação de microssegundos em vez de milissegundos.
 */
@Service
@RequiredArgsConstructor
public class AStar {

    private static final double ROAD_COST_PER_KM    = 5.00;
    private static final double RAILWAY_COST_PER_KM = 1.20;
    private static final double TRANSFER_PENALTY    = 1_000.00;
    private static final double EARTH_RADIUS_KM     = 6_371.0;

    // Repositórios — consultados SOMENTE no @PostConstruct, nunca depois
    private final CapitalRepository capitalRepository;
    private final PathBetweenCapitalsRepository pathRepository;

    // Dados base — carregados uma vez, nunca mais alterados

    /** Mapa de capitais: sigla → Capital (latitude, longitude, nome) */
    private Map<String, Capital> capitalMap;

    /**
     * Lista compacta de todas as arestas do banco, sem nenhum objeto JPA.
     * Usada pelo AG para remontar o grafo em memória sem tocar o banco.
     * Formato: [origemId, destinoId, distanciaKm]
     */
    private List<int[]> rawPaths;           // int[0]=hash origem, int[1]=hash destino, int[2]=km
    private List<String[]> rawPathIds;      // String[0]=origemId, String[1]=destinoId

    /**
     * Grafo base com apenas rodovias (sem nenhuma ferrovia).
     * Imutável após o startup — nunca sobrescrito.
     * O AG parte deste grafo e adiciona ferrovias por cima.
     */
    private Map<String, List<Edge>> roadOnlyGraph;

    // Grafo ativo — o que o findRoute() usa na busca

    /** Grafo atual em uso: pode ser road-only, kruskal ou cromossomo do AG */
    private Map<String, List<Edge>> graph;

    // Startup — lê o banco uma vez

    /**
     * Executado uma vez ao subir a aplicação.
     * Carrega tudo do banco, monta o grafo base de rodovias e os dados raw para o AG.
     * Após este método, ZERO queries SQL são feitas pelo A*.
     */
    @PostConstruct
    public void initGraph() {
        List<Capital> capitals          = capitalRepository.findAll();
        List<PathBetweenCapitals> paths = pathRepository.findAll();

        // 1. Mapa de capitais por sigla
        capitalMap = new HashMap<>(capitals.size() * 2);
        for (Capital c : capitals) {
            capitalMap.put(c.getId(), c);
        }

        // 2. Dados raw para o AG (sem objetos JPA — só Strings e ints)
        rawPaths   = new ArrayList<>(paths.size());
        rawPathIds = new ArrayList<>(paths.size());
        for (PathBetweenCapitals p : paths) {
            rawPathIds.add(new String[]{ p.getOrigin().getId(), p.getDestination().getId() });
            rawPaths.add(new int[]{ p.getDistance() });
        }

        // 3. Grafo base: só rodovias (has_railway ignorado aqui)
        roadOnlyGraph = new HashMap<>(capitals.size() * 2);
        for (Capital c : capitals) {
            roadOnlyGraph.put(c.getId(), new ArrayList<>());
        }
        for (int i = 0; i < rawPathIds.size(); i++) {
            String from = rawPathIds.get(i)[0];
            String to   = rawPathIds.get(i)[1];
            int    km   = rawPaths.get(i)[0];
            roadOnlyGraph.get(from).add(new Edge(to, km, TransportMode.ROAD));
        }

        // 4. Grafo ativo começa como road-only
        graph = roadOnlyGraph;
    }

    // Modos de operação — cada um configura o "graph" ativo

    /**
     * Item b — ativa o modo somente rodovias.
     * Operação O(1): apenas aponta o graph para o roadOnlyGraph já existente.
     */
    public void useRoadOnlyGraph() {
        graph = roadOnlyGraph;
    }

    /**
     * Item d — recarrega has_railway do banco e reconstrói o grafo.
     * Faz query SQL — deve ser chamado apenas uma vez após o Kruskal executar.
     * Depois disso, o grafo fica em memória e findRoute() não faz mais queries.
     */
    public void reloadFromDatabase() {
        List<PathBetweenCapitals> paths = pathRepository.findAll();

        Map<String, List<Edge>> newGraph = new HashMap<>(capitalMap.size() * 2);
        for (String id : capitalMap.keySet()) {
            newGraph.put(id, new ArrayList<>());
        }

        for (PathBetweenCapitals p : paths) {
            String  from    = p.getOrigin().getId();
            String  to      = p.getDestination().getId();
            int     km      = p.getDistance();
            boolean railway = Boolean.TRUE.equals(p.getHasRailway());

            newGraph.get(from).add(new Edge(to, km, TransportMode.ROAD));
            if (railway) {
                newGraph.get(from).add(new Edge(to, km, TransportMode.RAILWAY));
            }
        }

        graph = newGraph;
    }

    /**
     * Item f — constrói o grafo para um cromossomo do Algoritmo Genético.
     *
     * OTIMIZADO: usa os dados raw já em memória (rawPathIds + rawPaths).
     * NÃO faz nenhuma query SQL. Cria um novo Map em memória com rodovias +
     * as ferrovias do cromossomo. Tempo: O(arestas) ~ microssegundos.
     *
     * Chamado pelo AG uma vez por cromossomo por geração.
     * Com 100 indivíduos × 200 gerações = 20.000 chamadas — todas em memória.
     *
     * @param railwayEdges conjunto "ORIGEM-DESTINO" com ferrovias ativas no cromossomo
     *                     Exemplo: {"SP-RJ", "RJ-SP", "MG-RJ", "RJ-MG"}
     */
    public void buildGraphForRailways(Set<String> railwayEdges) {
        // Aloca novo grafo em memória — sem tocar o banco
        Map<String, List<Edge>> newGraph = new HashMap<>(capitalMap.size() * 2);
        for (String id : capitalMap.keySet()) {
            newGraph.put(id, new ArrayList<>());
        }

        // Percorre os dados raw (já em memória desde o startup)
        for (int i = 0; i < rawPathIds.size(); i++) {
            String from    = rawPathIds.get(i)[0];
            String to      = rawPathIds.get(i)[1];
            int    km      = rawPaths.get(i)[0];
            String edgeKey = from + "-" + to;

            // Rodovia sempre presente
            newGraph.get(from).add(new Edge(to, km, TransportMode.ROAD));

            // Ferrovia somente se o cromossomo incluiu esta aresta
            if (railwayEdges.contains(edgeKey)) {
                newGraph.get(from).add(new Edge(to, km, TransportMode.RAILWAY));
            }
        }

        graph = newGraph;
    }

    /**
     * Atalho mantido para compatibilidade com código existente.
     * Internamente chama buildGraphForRailways().
     */
    public void rebuildGraphWithRailways(Set<String> railwayEdges) {
        buildGraphForRailways(railwayEdges);
    }

    // Busca A*

    /**
     * Encontra a rota de menor custo financeiro entre duas capitais.
     * Opera sobre o grafo ativo (graph) sem nenhuma query SQL.
     *
     * @param originId      sigla da capital de origem (ex: "SP")
     * @param destinationId sigla da capital de destino (ex: "RJ")
     * @return AStarResult com custo e rota, ou AStarResult.empty() se não houver caminho
     */
    public AStarResult findRoute(String originId, String destinationId) {

        if (!graph.containsKey(originId) || !graph.containsKey(destinationId)) {
            return AStarResult.empty();
        }

        if (originId.equals(destinationId)) {
            return new AStarResult(0.0, List.of(), List.of(capitalMap.get(originId)));
        }

        Capital destination = capitalMap.get(destinationId);

        // Fila de prioridade: processa sempre o estado com menor f(n) = g(n) + h(n)
        PriorityQueue<SearchState> openSet = new PriorityQueue<>(
                Comparator.comparingDouble(s -> s.gCost + s.hCost)
        );

        // Menor custo já registrado por estado (capital + modal de chegada)
        Map<String, Double> bestCost = new HashMap<>();

        SearchState initial = new SearchState(
                originId, null, null,
                0.0,
                haversineFinancial(capitalMap.get(originId), destination),
                TransportMode.NONE
        );

        openSet.add(initial);
        bestCost.put(stateKey(originId, TransportMode.NONE), 0.0);

        while (!openSet.isEmpty()) {
            SearchState current = openSet.poll();

            if (current.capitalId.equals(destinationId)) {
                return buildResult(current);
            }

            String currentKey = stateKey(current.capitalId, current.arrivalMode);
            Double recorded   = bestCost.get(currentKey);
            if (recorded != null && current.gCost > recorded + 1e-9) {
                continue;
            }

            for (Edge edge : graph.getOrDefault(current.capitalId, Collections.emptyList())) {

                double edgeCost = (edge.mode == TransportMode.RAILWAY)
                        ? edge.distanceKm * RAILWAY_COST_PER_KM
                        : edge.distanceKm * ROAD_COST_PER_KM;

                double transferCost = 0.0;
                if (current.arrivalMode != TransportMode.NONE
                        && current.arrivalMode != edge.mode) {
                    transferCost = TRANSFER_PENALTY;
                }

                double newG = current.gCost + edgeCost + transferCost;

                String neighborKey = stateKey(edge.targetId, edge.mode);
                Double bestG       = bestCost.get(neighborKey);

                if (bestG == null || newG < bestG - 1e-9) {
                    bestCost.put(neighborKey, newG);

                    Capital neighbor = capitalMap.get(edge.targetId);
                    double  h        = haversineFinancial(neighbor, destination);

                    openSet.add(new SearchState(
                            edge.targetId, current, edge, newG, h, edge.mode
                    ));
                }
            }
        }

        return AStarResult.empty();
    }

    // Heurística admissível — Haversine × menor custo possível

    private double haversineFinancial(Capital from, Capital to) {
        double km = haversineKm(
                from.getLatitude().doubleValue(),  from.getLongitude().doubleValue(),
                to.getLatitude().doubleValue(),    to.getLongitude().doubleValue()
        );
        return km * RAILWAY_COST_PER_KM;
    }

    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                  * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    // Helpers internos

    private static String stateKey(String capitalId, TransportMode mode) {
        return capitalId + "|" + mode.name();
    }

    private AStarResult buildResult(SearchState finalState) {
        LinkedList<Edge>    edges = new LinkedList<>();
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

    public enum TransportMode {
        ROAD,    // Caminhão — R$ 5,00/km
        RAILWAY, // Trem     — R$ 1,20/km
        NONE     // Sentinela: estado inicial
    }

    public static class Edge {
        public final String        targetId;
        public final int           distanceKm;
        public final TransportMode mode;

        public Edge(String targetId, int distanceKm, TransportMode mode) {
            this.targetId   = targetId;
            this.distanceKm = distanceKm;
            this.mode       = mode;
        }
    }

    public static class SearchState {
        public final String        capitalId;
        public final SearchState   previous;
        public final Edge          edgeUsed;
        public final double        gCost;
        public final double        hCost;
        public final TransportMode arrivalMode;

        public SearchState(String capitalId, SearchState previous, Edge edgeUsed,
                           double gCost, double hCost, TransportMode arrivalMode) {
            this.capitalId   = capitalId;
            this.previous    = previous;
            this.edgeUsed    = edgeUsed;
            this.gCost       = gCost;
            this.hCost       = hCost;
            this.arrivalMode = arrivalMode;
        }
    }

    public static class AStarResult {
        public final double        totalCostBrl;
        public final List<Edge>    edges;
        public final List<Capital> route;
        public final boolean       found;

        public AStarResult(double totalCostBrl, List<Edge> edges, List<Capital> route) {
            this.totalCostBrl = totalCostBrl;
            this.edges        = edges;
            this.route        = route;
            this.found        = true;
        }

        private AStarResult() {
            this.totalCostBrl = 0.0;
            this.edges        = Collections.emptyList();
            this.route        = Collections.emptyList();
            this.found        = false;
        }

        public static AStarResult empty() {
            return new AStarResult();
        }
    }
}