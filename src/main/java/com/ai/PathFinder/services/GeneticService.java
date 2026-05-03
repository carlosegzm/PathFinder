package com.ai.PathFinder.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@DependsOn("flyway")
public class GeneticService {

    private final PathBetweenCapitalsRepository pathRepository;
    private final CommonRouteRepository commonRouteRepository;
    private GeneticResponseDto cachedDefaultResult;

    // Otimização com postconstruct
    private List<Edge> allPossibleEdges;
    private List<Demand> demands;

    /**
     * Carrega e pré-processa os dados geográficos e de demanda uma única vez na
     * inicialização da aplicação.
     * Este método é acionado assim que o contexto da aplicação Spring está pronto
     * para uso.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        this.allPossibleEdges = loadAllPossibleRailways();
        this.demands = loadDemands();
    }

    /**
     * Executa o processo de otimização da malha ferroviária através do Algoritmo
     * Genético.
     * O método inclui uma lógica de cache para requisições com parâmetros padrão,
     * valida o limite
     * de orçamento e converte o resultado do melhor cromossomo para um DTO de
     * resposta.
     * 
     * @param request Objeto contendo os parâmetros da simulação (tamanho da
     *                população, gerações, orçamento, etc.).
     * 
     * @return Um objeto contendo o custo total de transporte, custo de construção e
     *         as ferrovias sugeridas.
     * 
     * @throws IllegalArgumentException Se o limite de orçamento for nulo.
     */
    public GeneticResponseDto runOptimization(GeneticRequestDto request) {

        if (request.budgetLimit() == null) {
            throw new IllegalArgumentException("budgetLimit must not be null");
        }

        boolean isDefaultRequest = request.popSize() == 200 &&
                request.generations() == 100 &&
                Math.abs(request.mutationRate() - 0.03) < 0.000001 &&
                request.tournamentSize() == 3;

        if (isDefaultRequest && cachedDefaultResult != null) {
            return cachedDefaultResult;
        }

        // BudgetLimit: Define o orçamento (Ex: 60% do custo do Kruskal)
        double budgetLimit = request.budgetLimit().doubleValue();

        FitnessEvaluator evaluator = new FitnessEvaluator(demands, budgetLimit, allPossibleEdges);
        GeneticAlgorithm ga = new GeneticAlgorithm(allPossibleEdges, evaluator);

        // Roda o GA
        Cromossome winner = ga.run(
                request.popSize(),
                request.generations(),
                request.mutationRate(),
                request.tournamentSize());

        // Converte para o DTO
        List<String> railwayIds = winner.getFerrovias().stream()
                .map(e -> {
                    String id1 = e.getFrom().getId();
                    String id2 = e.getTo().getId();
                    // ordem lexicográfica
                    return (id1.compareTo(id2) < 0) ? id1 + "-" + id2 : id2 + "-" + id1;
                })
                .distinct() // Remove duplicatas como SP-RJ e RJ-SP
                .toList();

        GeneticResponseDto response = new GeneticResponseDto(
                winner.getTotalTransportCost(),
                winner.getConstructionCost(),
                budgetLimit,
                railwayIds);

        if (isDefaultRequest) {
            cachedDefaultResult = response;
        }

        return response;
    }

    /**
     * Busca no repositório todos os caminhos possíveis entre capitais e os converte
     * para o modelo de arestas do grafo.
     * 
     * @return Uma lista de objetos Edge representando todas as conexões
     *         ferroviárias candidatas.
     */
    private List<Edge> loadAllPossibleRailways() {
        List<PathBetweenCapitals> paths = pathRepository.findAll();
        List<Edge> egdes = new ArrayList<>();

        for (PathBetweenCapitals p : paths) {
            egdes.add(Adapter.fromPath(p));
        }

        return egdes;
    }

    /**
     * Recupera todas as rotas comuns do banco de dados e as transforma em objetos
     * de Demanda.
     * Essas demandas são utilizadas para calcular o fluxo e o custo de transporte
     * no avaliador de fitness.
     * 
     * @return Uma lista de demandas contendo origem, destino e quantidade de
     *         carga.
     */
    private List<Demand> loadDemands() {
        List<CommonRoute> commonRoutes = commonRouteRepository.findAll();
        List<Demand> demands = new ArrayList<>();

        for (CommonRoute cr : commonRoutes) {
            demands.add(Adapter.fromCommonRoute(cr));
        }

        return demands;
    }
}
