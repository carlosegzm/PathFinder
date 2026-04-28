package com.ai.PathFinder.controllers;

import com.ai.PathFinder.dtos.Astar.AStarRequestDto;
import com.ai.PathFinder.dtos.Astar.AStarResponseDto;
import com.ai.PathFinder.dtos.Astar.GeneticAStarRequestDto;
import com.ai.PathFinder.dtos.Astar.KruskalAStarRequestDto;
import com.ai.PathFinder.services.AStarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Controller REST do algoritmo A*.
 *
 * Endpoints:
 *
 *   POST /api/astar/road
 *     Item b: menor rota usando apenas rodovias (R$ 5,00/km).
 *     Body: { "origin": "SP", "destination": "RJ" }
 *
 *   POST /api/astar/kruskal
 *     Item d: menor rota com a malha ferroviária do Kruskal.
 *     Pré-condição: chamar POST /api/kruskal/execute antes.
 *     Body: { "origin": "SP", "destination": "RJ" }
 *
 *   POST /api/astar/genetic
 *     Item f: menor rota com a malha ferroviária do Algoritmo Genético.
 *     Body: { "origin": "SP", "destination": "AM", "railwayEdges": ["SP-RJ","RJ-SP"] }
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/astar")
public class AStarController {

    private final AStarService aStarService;

    // ------------------------------------------------------------------
    // Item b — apenas rodovias
    // ------------------------------------------------------------------

    @PostMapping("/road")
    public ResponseEntity<AStarResponseDto> findRoadRoute(
            @Valid @RequestBody AStarRequestDto request) {

        AStarResponseDto response = aStarService.findRoadOnlyRoute(request);

        return response.routeFound()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(404).body(response);
    }

    // ------------------------------------------------------------------
    // Item d — malha ferroviária do Kruskal
    // ------------------------------------------------------------------

    @PostMapping("/kruskal")
    public ResponseEntity<AStarResponseDto> findKruskalRoute(
            @Valid @RequestBody KruskalAStarRequestDto request) {

        AStarResponseDto response = aStarService.findKruskalRoute(request);

        return response.routeFound()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(404).body(response);
    }

    // ------------------------------------------------------------------
    // Item f — malha ferroviária do Algoritmo Genético
    // ------------------------------------------------------------------

    @PostMapping("/genetic")
    public ResponseEntity<AStarResponseDto> findGeneticRoute(
            @Valid @RequestBody GeneticAStarRequestDto request) {

        AStarRequestDto baseRequest = new AStarRequestDto(
                request.getOrigin(),
                request.getDestination()
        );

        Set<String> railways = (request.getRailwayEdges() != null)
                ? request.getRailwayEdges()
                : Set.of();

        AStarResponseDto response = aStarService.findGeneticRoute(baseRequest, railways);

        return response.routeFound()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(404).body(response);
    }
}