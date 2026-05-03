package com.ai.PathFinder.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ai.PathFinder.dtos.genetic.GeneticRequestDto;
import com.ai.PathFinder.dtos.genetic.GeneticResponseDto;
import com.ai.PathFinder.services.GeneticService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/genetic")
public class GeneticController {

    private final GeneticService geneticService;

    /**
     * Endpoint responsável por iniciar o processo de otimização da malha
     * ferroviária através do algoritmo genético.
     * Recebe parâmetros de configuração da população e limites orçamentais para
     * encontrar a solução de melhor custo-benefício.
     * 
     * @param request Objeto DTO contendo as configurações da simulação (tamanho
     *                da população,
     *                taxa de mutação, gerações e limite de orçamento).
     * 
     * @return ResponseEntity contendo o DTO de resposta com a melhor malha
     *         encontrada e seus custos.
     */
    @PostMapping("/run")
    public ResponseEntity<GeneticResponseDto> executeOptimization(@Valid @RequestBody GeneticRequestDto request) {
        GeneticResponseDto result = geneticService.runOptimization(request);
        return ResponseEntity.ok(result);
    }
}
