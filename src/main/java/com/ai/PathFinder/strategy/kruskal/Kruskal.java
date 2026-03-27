package com.ai.PathFinder.strategy.kruskal;

import com.ai.PathFinder.entities.Capital;
import com.ai.PathFinder.entities.PathBetweenCapitals;
import com.ai.PathFinder.repositories.CapitalRepository;
import com.ai.PathFinder.repositories.PathBetweenCapitalsRepository;
import com.ai.PathFinder.dtos.kruskal.KruskalResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class Kruskal {

    @Autowired
    private PathBetweenCapitalsRepository pathRepository;

    @Autowired
    private CapitalRepository capitalRepository;

    public KruskalResponseDto executeKruskal() {

        List<PathBetweenCapitals> allPaths = pathRepository.findAll();
        List<Capital> allCapitals = capitalRepository.findAll();

        UnionFind uf = new UnionFind();
        for (Capital capital : allCapitals) {
            uf.makeSet(capital.getId());
        }

        //ordena menor distância p maior
        allPaths.sort(Comparator.comparing(PathBetweenCapitals::getDistance));

        double totalDistance = 0;

        for (PathBetweenCapitals path : allPaths) {
            String originId = path.getOrigin().getId();
            String destinationId = path.getDestination().getId();

            if (!uf.find(originId).equals(uf.find(destinationId))) {

                uf.union(originId, destinationId);

                path.setHasRailway(true);
                pathRepository.save(path);
                totalDistance += path.getDistance();
            } else {
                path.setHasRailway(false);
                pathRepository.save(path);
            }
        }

        BigDecimal distance = BigDecimal.valueOf(totalDistance);
        BigDecimal costPerKm = new BigDecimal("2000000.00"); //2.000.000 por km

        BigDecimal totalConstructionCost = distance.multiply(costPerKm);

        BigDecimal percentage = new BigDecimal("0.60"); //60% custo total
        BigDecimal availableBudgetForGenetics = totalConstructionCost.multiply(percentage);

        KruskalResponseDto response = new KruskalResponseDto(totalDistance, totalConstructionCost, availableBudgetForGenetics);

        return response;
    }

}