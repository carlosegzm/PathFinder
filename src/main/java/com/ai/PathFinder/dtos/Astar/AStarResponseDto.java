package com.ai.PathFinder.dtos.Astar;

import java.math.BigDecimal;
import java.util.List;

/**
 * Resposta completa retornada pelos endpoints do A*.
 *
 * Exemplo de JSON para SP → RJ em modo road-only:
 * {
 *   "originId": "SP",
 *   "originName": "São Paulo",
 *   "destinationId": "RJ",
 *   "destinationName": "Rio de Janeiro",
 *   "totalDistanceKm": 435,
 *   "totalCostBrl": 2175.00,
 *   "totalTransfers": 0,
 *   "totalTransferCostBrl": 0.00,
 *   "mode": "road-only",
 *   "routeFound": true,
 *   "segments": [
 *     {
 *       "fromId": "SP", "fromName": "São Paulo",
 *       "toId": "RJ", "toName": "Rio de Janeiro",
 *       "distanceKm": 435,
 *       "transportMode": "ROAD",
 *       "hasTransfer": false,
 *       "segmentCostBrl": 2175.00
 *     }
 *   ]
 * }
 *
 * Quando não há rota: routeFound = false, segments = [], totalCostBrl = 0.
 */
public record AStarResponseDto(
        String     originId,
        String     originName,
        String     destinationId,
        String     destinationName,
        int        totalDistanceKm,
        BigDecimal totalCostBrl,
        int        totalTransfers,
        BigDecimal totalTransferCostBrl,
        String     mode,          // "road-only" | "kruskal-railways" | "genetic-railways"
        boolean    routeFound,
        List<RouteSegmentDto> segments
) {
    /**
     * Factory method para quando não existe rota entre as cidades solicitadas.
     */
    public static AStarResponseDto notFound(String mode) {
        return new AStarResponseDto(
                null, null, null, null,
                0, java.math.BigDecimal.ZERO,
                0, java.math.BigDecimal.ZERO,
                mode, false, List.of()
        );
    }
}