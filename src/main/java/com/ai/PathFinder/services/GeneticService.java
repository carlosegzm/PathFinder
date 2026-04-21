package com.ai.PathFinder.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ai.PathFinder.dtos.genetic.GeneticRequestDto;
import com.ai.PathFinder.dtos.genetic.GeneticResponseDto;
import com.ai.PathFinder.entities.CommonRoute;
import com.ai.PathFinder.entities.PathBetweenCapitals;
import com.ai.PathFinder.repositories.CommonRouteRepository;
import com.ai.PathFinder.repositories.PathBetweenCapitalsRepository;
import com.ai.PathFinder.strategy.genetic.Cromossome;
import com.ai.PathFinder.strategy.genetic.Demand;
import com.ai.PathFinder.strategy.genetic.FitnessEvaluator;
import com.ai.PathFinder.strategy.genetic.GeneticAlgorithm;
import com.ai.PathFinder.strategy.graph.Adapter;
import com.ai.PathFinder.strategy.graph.Edge;
import com.ai.PathFinder.strategy.search.AStar;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeneticService {

    private final PathBetweenCapitalsRepository pathRepository;
    private final CommonRouteRepository commonRouteRepository;
    private final AStar aStar;

    // Otimização com postconstruct
    private List<Edge> allPossibleEdges;
    private List<Demand> demands;

    /**
     * Carrega os dados uma única vez na inicialização da aplicação.
     */
    @PostConstruct
    public void init() {
        this.allPossibleEdges = loadAllPossibleRailways();
        this.demands = loadDemands();
    }

    public GeneticResponseDto runOptimization(GeneticRequestDto request) {
        
        if (request.budgetLimit() == null) {
            throw new IllegalArgumentException("budgetLimit must not be null");
        }

        // BudgetLimit: Define o orçamento (Ex: 60% do custo do Kruskal)
        double budgetLimit = request.budgetLimit().doubleValue();

        FitnessEvaluator evaluator = new FitnessEvaluator(aStar, demands, budgetLimit);
        GeneticAlgorithm ga = new GeneticAlgorithm(allPossibleEdges, evaluator);

        // Roda o GA
        Cromossome winner = ga.run(
            request.popSize(), 
            request.generations(), 
            request.mutationRate(),
            request.tournamentSize()
        );

        // Converte para o DTO
        List<String> railwayIds = winner.getFerrovias().stream()
                .map(e -> e.getFrom().getId() + "-" + e.getTo().getId())
                .toList();

        double constructionCost = winner.getFerrovias().stream()
                .mapToDouble(e -> e.getDistance() * 2_000_000)
                .sum();

        return new GeneticResponseDto(
                winner.getFitness(),
                constructionCost,
                budgetLimit,
                railwayIds);
    }

    private List<Edge> loadAllPossibleRailways() {
        List<PathBetweenCapitals> paths = pathRepository.findAll();
        List<Edge> egdes = new ArrayList<>();

        for (PathBetweenCapitals p : paths) {
            egdes.add(Adapter.fromPath(p));
        }

        return egdes;
    }

    private List<Demand> loadDemands() {
        List<CommonRoute> commonRoutes = commonRouteRepository.findAll();
        List<Demand> demands = new ArrayList<>();

        for (CommonRoute cr : commonRoutes) {
            demands.add(Adapter.fromCommonRoute(cr));
        }

        return demands;
    }
}
