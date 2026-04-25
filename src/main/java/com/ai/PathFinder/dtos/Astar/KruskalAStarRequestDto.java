package com.ai.PathFinder.dtos.Astar;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 
 * DTO para o endpoints de kruskal do A*.*
 * Exemplo de body JSON:
 * {
 * "origin": "SP",
 * "destination": "RJ"
 * "railwayNetwork": [
 *	    "MT-RO",
 *	    "AL-SE",
 *	]
 * }
 */
public record KruskalAStarRequestDto(
        @NotBlank(message = "origin é obrigatório") 
        @Size(min = 2, max = 2, message = "origin deve ser a sigla do estado com 2 letras") 
        String origin,

        @NotBlank(message = "destination é obrigatório") 
        @Size(min = 2, max = 2, message = "destination deve ser a sigla do estado com 2 letras")
        String destination,

        Set<String> railwayNetwork
) {
    public KruskalAStarRequestDto{
        origin.toUpperCase();
        destination.toUpperCase();
    }
}