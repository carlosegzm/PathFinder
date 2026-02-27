package com.ai.PathFinder.services;

import com.ai.PathFinder.dtos.PathBetweenCapitalsDto;
import com.ai.PathFinder.mappers.PathBetweenCapitalsMapper;
import com.ai.PathFinder.repositories.PathBetweenCapitalsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PathBetweenCapitalsService {

    private final PathBetweenCapitalsRepository repository;
    private final PathBetweenCapitalsMapper mapper;

    public List<PathBetweenCapitalsDto> listAllPathsBetweenCapitals(){
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }





}
