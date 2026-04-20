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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeneticService {

    private final PathBetweenCapitalsRepository pathRepository;
    private final CommonRouteRepository commonRouteRepository;
    private final AStar aStar;

    public GeneticResponseDto runOptimization(GeneticRequestDto request) {
        if (request.getBudgetLimit() == null) {
            throw new IllegalArgumentException("budgetLimit must not be null");
        }

        // BudgetLimit: Define o orçamento (Ex: 60% do custo do Kruskal)
        double budgetLimit = request.getBudgetLimit().doubleValue();

        // Carrega as arestas que podem virar ferrovias (as do Kruskal ou todas)
        // Aqui assumimos que você quer otimizar sobre os caminhos existentes
        List<Edge> allPossibleEdges = loadAllPossibleRailways();

        // Configura as demandas (As 50 rotas do Anexo I)
        List<Demand> demands = loadDemands();

        // Inicializa o avaliador e o AG
        FitnessEvaluator evaluator = new FitnessEvaluator(aStar, demands, budgetLimit);
        GeneticAlgorithm ga = new GeneticAlgorithm(allPossibleEdges, evaluator);

        // Roda o algoritmo (População 100, 50 Gerações)
        Cromossome winner = ga.run(10000, 50);

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
