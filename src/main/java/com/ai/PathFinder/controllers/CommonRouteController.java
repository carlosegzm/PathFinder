package com.ai.PathFinder.controllers;

import com.ai.PathFinder.dtos.CommonRouteDto;
import com.ai.PathFinder.services.CommonRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/routes")
public class CommonRouteController {

    private final CommonRouteService service;

    @GetMapping
    public ResponseEntity<List<CommonRouteDto>> listAllCommonRoutes(){
        List<CommonRouteDto> commonRoutesList = service.listAllCommonRoutes();
        return ResponseEntity.ok(commonRoutesList);
    }





}
