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

    @GetMapping
    public ResponseEntity<KruskalResponseDto> gerarMalha() {

        // Executa o algoritmo e pega o relatório com as Strings das rotas
        KruskalResponseDto relatorioFinal = kruskal.executeKruskal();

        return ResponseEntity.ok(relatorioFinal);
    }
}
