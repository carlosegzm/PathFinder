package com.ai.PathFinder.services;

import com.ai.PathFinder.dtos.Astar.AStarRequestDto;
import com.ai.PathFinder.dtos.Astar.AStarResponseDto;
import com.ai.PathFinder.dtos.Astar.KruskalAStarRequestDto;
import com.ai.PathFinder.dtos.Astar.RouteSegmentDto;
import com.ai.PathFinder.entities.Capital;
import com.ai.PathFinder.strategy.search.AStar;
import com.ai.PathFinder.strategy.search.AStar.AStarResult;
import com.ai.PathFinder.strategy.search.AStar.Edge;
import com.ai.PathFinder.strategy.search.AStar.TransportMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Serviço que orquestra as chamadas ao algoritmo A*.
 *
 * Expõe três modos de operação conforme os itens do trabalho:
 * - road: item b — só rodovias, R$ 5,00/km
 * - kruskal: item d — malha do Kruskal salva no banco (has_railway = true)
 * - genetic: item f — malha definida pelo Algoritmo Genético
 *
 * Este serviço não faz nenhuma busca — apenas configura o grafo no AStar
 * e converte o resultado interno (AStarResult) para o DTO da API
 * (AStarResponseDto).
 */
@Service
@RequiredArgsConstructor
public class AStarService {

    private final AStar aStar;

    // Item b — apenas rodovias

    /**
     * Rota mais barata usando somente rodovias.
     * O grafo é reconstruído sem nenhuma ferrovia antes da busca.
     */
    public AStarResponseDto findRoadOnlyRoute(AStarRequestDto request) {
        AStarResult result = aStar.findRoute(request.getOrigin(), request.getDestination(), new HashSet<>());
        return toResponseDto(result, "road-only");
    }

    // -------------------------------------------------------------------------
    // Item d — malha do Kruskal (has_railway salvo no banco após executar o
    // Kruskal)
    // -------------------------------------------------------------------------

    /**
     * Rota mais barata com a malha ferroviária do Kruskal.
     *
     * Chama initGraph() para garantir que o grafo reflete o has_railway
     * atual do banco (atualizado quando o endpoint do Kruskal foi chamado).
     *
     * IMPORTANTE: chame POST /api/kruskal/execute antes de usar este endpoint.
     */
    public AStarResponseDto findKruskalRoute(KruskalAStarRequestDto request) {
        aStar.rebuildGraphWithRailways(request.railwayNetwork());
        AStarResult result = aStar.findRoute(request.origin(), request.destination(), request.railwayNetwork());
        return toResponseDto(result, "kruskal-railways");
    }

    // Item f — malha do Algoritmo Genético

    /**
     * Rota mais barata com a malha ferroviária definida pelo Algoritmo Genético.
     * buildGraphForRailways() opera 100% em memória
     *
     * @param request      origem e destino
     * @param railwayEdges arestas ferroviárias ativas no cromossomo (ex:
     *                     {"SP-RJ","RJ-SP"})
     */
    public AStarResponseDto findGeneticRoute(AStarRequestDto request, Set<String> railwayEdges) {
        AStarResult result = aStar.findRoute(request.getOrigin(), request.getDestination(), railwayEdges);
        return toResponseDto(result, "genetic-railways");
    }

    // Conversão AStarResult → AStarResponseDto

    private AStarResponseDto toResponseDto(AStarResult result, String mode) {
        if (!result.found) {
            return AStarResponseDto.notFound(mode);
        }

        List<RouteSegmentDto> segments = new ArrayList<>();
        List<Edge> edges = result.edges;
        List<Capital> route = result.route;

        int transfers = 0;
        TransportMode previousMode = null;

        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            Capital from = route.get(i);
            Capital to = route.get(i + 1);

            // Verifica se houve troca de modal em relação ao trecho anterior
            boolean isTransfer = previousMode != null && previousMode != edge.mode;
            if (isTransfer)
                transfers++;

            // Custo do trecho sem transbordo
            double segmentCost = (edge.mode == TransportMode.RAILWAY)
                    ? edge.distanceKm * 1.20
                    : edge.distanceKm * 5.00;

            // Adiciona penalidade de transbordo se houver
            if (isTransfer)
                segmentCost += 1_000.0;

            segments.add(new RouteSegmentDto(
                    from.getId(),
                    from.getName(),
                    to.getId(),
                    to.getName(),
                    edge.distanceKm,
                    edge.mode.name(),
                    isTransfer,
                    round(segmentCost)));

            previousMode = edge.mode;
        }

        int totalKm = edges.stream().mapToInt(e -> e.distanceKm).sum();

        return new AStarResponseDto(
                route.get(0).getId(),
                route.get(0).getName(),
                route.get(route.size() - 1).getId(),
                route.get(route.size() - 1).getName(),
                totalKm,
                round(result.totalCostBrl),
                transfers,
                round(transfers * 1_000.0),
                mode,
                true,
                segments);
    }

    // Arredonda para 2 casas decimais. 
    private BigDecimal round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}