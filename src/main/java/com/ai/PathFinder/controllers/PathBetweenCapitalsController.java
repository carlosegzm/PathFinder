package com.ai.PathFinder.controllers;

import com.ai.PathFinder.dtos.PathBetweenCapitalsDto;
import com.ai.PathFinder.services.PathBetweenCapitalsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/paths")
public class PathBetweenCapitalsController {

    private final PathBetweenCapitalsService service;

    @GetMapping
    public ResponseEntity<List<PathBetweenCapitalsDto>> listAllPathsBetweenCapitals(){
        List<PathBetweenCapitalsDto> pathsBetweenCapitalsList = service.listAllPathsBetweenCapitals();
        return ResponseEntity.ok(pathsBetweenCapitalsList);
    }
    
}
