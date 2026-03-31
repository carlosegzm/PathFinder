package com.ai.PathFinder.dtos.Astar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para os endpoints do A*.
 *
 * Exemplo de body JSON:
 * {
 *   "origin": "SP",
 *   "destination": "RJ"
 * }
 */
public class AStarRequestDto {

    @NotBlank(message = "origin é obrigatório")
    @Size(min = 2, max = 2, message = "origin deve ser a sigla do estado com 2 letras")
    private String origin;

    @NotBlank(message = "destination é obrigatório")
    @Size(min = 2, max = 2, message = "destination deve ser a sigla do estado com 2 letras")
    private String destination;

    // Construtor vazio necessário para o Jackson desserializar o JSON
    public AStarRequestDto() {}

    public AStarRequestDto(String origin, String destination) {
        this.origin      = origin.toUpperCase();
        this.destination = destination.toUpperCase();
    }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin.toUpperCase(); }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination.toUpperCase(); }
}