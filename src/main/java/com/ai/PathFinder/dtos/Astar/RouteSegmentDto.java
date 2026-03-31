package com.ai.PathFinder.dtos.Astar;

import java.math.BigDecimal;

/**
 * Representa um trecho individual da rota calculada pelo A*.
 *
 * Cada segmento é uma aresta do grafo percorrida durante a busca.
 * O campo hasTransfer indica se houve troca de modal ao entrar neste trecho
 * (nesse caso, R$ 1.000,00 de penalidade já está incluído em segmentCostBrl).
 *
 * Exemplo de JSON gerado:
 * {
 *   "fromId": "SP",
 *   "fromName": "São Paulo",
 *   "toId": "RJ",
 *   "toName": "Rio de Janeiro",
 *   "distanceKm": 435,
 *   "transportMode": "ROAD",
 *   "hasTransfer": false,
 *   "segmentCostBrl": 2175.00
 * }
 */
public record RouteSegmentDto(
        String     fromId,
        String     fromName,
        String     toId,
        String     toName,
        int        distanceKm,
        String     transportMode,  // "ROAD" ou "RAILWAY"
        boolean    hasTransfer,    // true se houve troca de modal neste trecho
        BigDecimal segmentCostBrl  // custo deste trecho (incluindo transbordo se houver)
) {}