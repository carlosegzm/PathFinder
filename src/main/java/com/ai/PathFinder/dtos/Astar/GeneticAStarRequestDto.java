package com.ai.PathFinder.dtos.Astar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * DTO de entrada para o endpoint POST /api/astar/genetic.
 *
 * Além da origem e destino, recebe o conjunto de ferrovias
 * que o Algoritmo Genético definiu para aquele cromossomo.
 *
 * Exemplo de body JSON:
 * {
 *   "origin": "SP",
 *   "destination": "AM",
 *   "railwayEdges": ["SP-RJ", "RJ-SP", "RJ-MG", "MG-RJ"]
 * }
 *
 * Cada string de railwayEdges segue o formato "SIGLA_ORIGEM-SIGLA_DESTINO".
 * Como as rotas são bidirecionais, inclua as duas direções: "SP-RJ" e "RJ-SP".
 */
public class GeneticAStarRequestDto {

    @NotBlank(message = "origin é obrigatório")
    @Size(min = 2, max = 2, message = "origin deve ser a sigla do estado com 2 letras")
    private String origin;

    @NotBlank(message = "destination é obrigatório")
    @Size(min = 2, max = 2, message = "destination deve ser a sigla do estado com 2 letras")
    private String destination;

    // Arestas ferroviárias ativas no cromossomo — pode ser null ou vazio
    private Set<String> railwayEdges;

    public GeneticAStarRequestDto() {}

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin.toUpperCase(); }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination.toUpperCase(); }

    public Set<String> getRailwayEdges() { return railwayEdges; }
    public void setRailwayEdges(Set<String> railwayEdges) { this.railwayEdges = railwayEdges; }
}