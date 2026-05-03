package com.ai.PathFinder.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ai.PathFinder.dtos.kruskal.KruskalResponseDto;
import com.ai.PathFinder.strategy.kruskal.Kruskal;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/kruskal")
public class KruskalController {

    private final Kruskal kruskal;

    /**
     * Endpoint responsável por gerar a Árvore Geradora Mínima (MST) da malha
     * ferroviária utilizando o algoritmo de Kruskal.
     * Este método foca em conectar todos os pontos com o menor custo de construção
     * possível, sem considerar necessariamente as demandas de transporte individuais.
     * 
     * @return ResponseEntity contendo um relatório com as rotas que compõem a
     *         malha mínima e o custo total.
     */
    @GetMapping
    public ResponseEntity<KruskalResponseDto> gerarMalha() {
        KruskalResponseDto relatorioFinal = kruskal.executeKruskal();

        return ResponseEntity.ok(relatorioFinal);
    }
}
