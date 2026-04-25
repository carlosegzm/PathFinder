package com.ai.PathFinder.strategy.kruskal;

import com.ai.PathFinder.entities.Capital;
import com.ai.PathFinder.entities.PathBetweenCapitals;
import com.ai.PathFinder.repositories.CapitalRepository;
import com.ai.PathFinder.repositories.PathBetweenCapitalsRepository;

import lombok.RequiredArgsConstructor;

import com.ai.PathFinder.dtos.kruskal.KruskalResponseDto;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class Kruskal {

    private final PathBetweenCapitalsRepository pathRepository;
    private final CapitalRepository capitalRepository;

    // Otimização na inicialização
    private List<PathBetweenCapitals> allPaths;
    private List<Capital> allCapitals;

    @EventListener(ApplicationReadyEvent.class)
    public void init(){
        this.allPaths = pathRepository.findAll();
        this.allCapitals = capitalRepository.findAll();
    }

    public KruskalResponseDto executeKruskal() {

        UnionFind uf = new UnionFind();
        for (Capital capital : allCapitals) {
            uf.makeSet(capital.getId());
        }

        //ordena menor distância p maior
        allPaths.sort(Comparator.comparing(PathBetweenCapitals::getDistance));

        double totalDistance = 0;

        Set<String> formattedRailwayNetwork = new HashSet<>();

        for (PathBetweenCapitals path : allPaths) {
            String originId = path.getOrigin().getId();
            String destinationId = path.getDestination().getId();

            if (!uf.find(originId).equals(uf.find(destinationId))) {

                uf.union(originId, destinationId);

                path.setHasRailway(true);
                pathRepository.save(path);
                formattedRailwayNetwork.add(originId + "-" + destinationId);
                totalDistance += path.getDistance();
            } 
        }

        BigDecimal distance = BigDecimal.valueOf(totalDistance);
        BigDecimal costPerKm = new BigDecimal("2000000.00"); //2.000.000 por km

        BigDecimal totalConstructionCost = distance.multiply(costPerKm);

        BigDecimal percentage = new BigDecimal("0.60"); //60% custo total
        BigDecimal availableBudgetForGenetics = totalConstructionCost.multiply(percentage);

        KruskalResponseDto response = new KruskalResponseDto(totalDistance, totalConstructionCost, availableBudgetForGenetics, formattedRailwayNetwork);

        return response;
    }

}