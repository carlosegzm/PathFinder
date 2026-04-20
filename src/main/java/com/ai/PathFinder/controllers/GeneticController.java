package com.ai.PathFinder.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = "*")
public class GeneticController {

    private final GeneticService geneticService;

    @PostMapping("/run")
    public ResponseEntity<GeneticResponseDto> executeOptimization(@Valid @RequestBody GeneticRequestDto request) {
        System.out.println("Budget: " + request.getBudgetLimit());

        // esse endpoint processa e retorna a melhor malha.
        GeneticResponseDto result = geneticService.runOptimization(request);
        return ResponseEntity.ok(result);
    }
}
