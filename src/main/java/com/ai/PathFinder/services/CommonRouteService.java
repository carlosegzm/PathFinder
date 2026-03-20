package com.ai.PathFinder.services;

import com.ai.PathFinder.mappers.CommonRouteMapper;
import com.ai.PathFinder.repositories.CommonRouteRepository;
import com.ai.PathFinder.dtos.CommonRouteDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommonRouteService {

    private final CommonRouteRepository repository;
    private final CommonRouteMapper mapper;


    public List<CommonRouteDto> listAllCommonRoutes(){
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

}
