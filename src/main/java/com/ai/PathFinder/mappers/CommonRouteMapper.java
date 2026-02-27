package com.ai.PathFinder.mappers;

import com.ai.PathFinder.dtos.CommonRouteDto;
import com.ai.PathFinder.entities.CommonRoute;
import org.springframework.stereotype.Component;

@Component
public class CommonRouteMapper {

    public CommonRoute toEntity(CommonRouteDto requestDto) {
        CommonRoute entity = new CommonRoute();
        entity.setId(requestDto.id());
        entity.setOrigin(requestDto.origin());
        entity.setDestination(requestDto.destination());
        entity.setLoad(requestDto.load());
        return entity;
    }

    public CommonRouteDto toResponseDto(CommonRoute entity) {
        return new CommonRouteDto(
                entity.getId(),
                entity.getOrigin(),
                entity.getDestination(),
                entity.getLoad()
        );
    }

}
