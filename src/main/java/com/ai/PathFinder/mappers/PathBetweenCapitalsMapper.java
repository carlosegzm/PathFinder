package com.ai.PathFinder.mappers;

import com.ai.PathFinder.dtos.PathBetweenCapitalsDto;
import com.ai.PathFinder.entities.PathBetweenCapitals;
import org.springframework.stereotype.Component;

@Component
public class PathBetweenCapitalsMapper {

    public PathBetweenCapitals toEntity(PathBetweenCapitalsDto requestDto) {
        PathBetweenCapitals entity = new PathBetweenCapitals();
        entity.setId(requestDto.id());
        entity.setOrigin(requestDto.origin());
        entity.setDestination(requestDto.destination());
        entity.setDistance(requestDto.distance());
        return entity;
    }

    public PathBetweenCapitalsDto toResponseDto(PathBetweenCapitals entity) {
        return new PathBetweenCapitalsDto(
                entity.getId(),
                entity.getOrigin(),
                entity.getDestination(),
                entity.getDistance()
        );
    }

}
