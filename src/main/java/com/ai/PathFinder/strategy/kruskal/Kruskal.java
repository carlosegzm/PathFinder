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

    //guarda os dados na memória
    private List<PathBetweenCapitals> allPaths;
    private List<Capital> allCapitals;

    //busca no banco de dados qnd a aplicação liga
    @EventListener(ApplicationReadyEvent.class)
    public void init(){
        this.allPaths = pathRepository.findAll();
        this.allCapitals = capitalRepository.findAll();
    }

    public KruskalResponseDto executeKruskal() {

        UnionFind uf = new UnionFind();

        //coloca cada capital isolada no começo
        for (Capital capital : allCapitals) {
            uf.makeSet(capital.getId());
        }

        //ordena menor distância p maior
        allPaths.sort(Comparator.comparing(PathBetweenCapitals::getDistance));

        double totalDistance = 0;

        Set<String> railwayNetwork = new HashSet<>();

        //testa cada estrada da lista
        for (PathBetweenCapitals path : allPaths) {
            String originId = path.getOrigin().getId();
            String destinationId = path.getDestination().getId();

            //verifica se o destino e a origem já estão conectadas (p não criar ciclos)
            if (!uf.find(originId).equals(uf.find(destinationId))) {

                //une as duas capitais
                uf.union(originId, destinationId);

                String id1 = path.getOrigin().getId();
                String id2 = path.getDestination().getId();

                // padroniza a string da rota em ordem alfabética (ex: RS-SC)
                railwayNetwork.add(
                    (id1.compareTo(id2) < 0) ? id1 + "-" + id2 : id2 + "-" + id1
                );

                //soma a distância desse trecho na distância total
                totalDistance += path.getDistance();
            } 
        }

        // calcula o custo total da obra
        BigDecimal distance = BigDecimal.valueOf(totalDistance);
        BigDecimal costPerKm = new BigDecimal("2000000.00"); //2.000.000 por km

        BigDecimal totalConstructionCost = distance.multiply(costPerKm);

        // define que o orçamento para o Genético será 60%
        BigDecimal percentage = new BigDecimal("0.60"); //60% custo total
        BigDecimal availableBudgetForGenetics = totalConstructionCost.multiply(percentage);

        //prepara o response
        KruskalResponseDto response = new KruskalResponseDto(totalDistance, totalConstructionCost, availableBudgetForGenetics, railwayNetwork);

        return response;
    }

}