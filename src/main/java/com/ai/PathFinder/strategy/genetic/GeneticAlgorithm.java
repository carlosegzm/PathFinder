package com.ai.PathFinder.strategy.genetic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.ai.PathFinder.strategy.graph.Edge;

public class GeneticAlgorithm {

    private List<Edge> allEdges;
    private FitnessEvaluator evaluator;
    private Random random = new Random();

    /**
     * Inicializa o motor do algoritmo genético com as arestas disponíveis e o
     * avaliador de fitness.
     * 
     * @param allEdges  Lista de todas as arestas (caminhos possíveis) do grafo.
     * @param evaluator Instância responsável por calcular o custo e validar o
     *                  orçamento dos cromossomos.
     */
    public GeneticAlgorithm(List<Edge> allEdges, FitnessEvaluator evaluator) {
        this.allEdges = allEdges;
        this.evaluator = evaluator;
    }

    /**
     * Executa o ciclo completo do algoritmo genético através de múltiplas gerações.
     * Implementa elitismo (preservação do melhor indivíduo), seleção por torneio,
     * crossover e mutação.
     * 
     * @param popSize        Tamanho da população em cada geração.
     * @param generations    Número total de iterações/gerações a serem processadas.
     * @param mutationRate   Probabilidade de ocorrência de mutação em cada gene
     *                       (aresta).
     * @param tournamentSize Número de indivíduos que participam de cada rodada de
     *                       seleção por torneio.
     * @return O cromossomo com o melhor fitness (menor custo) encontrado ao final
     *         do processo.
     */
    public Cromossome run(int popSize, int generations, double mutationRate, int tournamentSize) {

        List<Cromossome> population = initPopulation(popSize);

        for (int gen = 0; gen < generations; gen++) {

            // log báico pra acompanhar o progresso do algoritmo
            if (gen % 10 == 0) {
                System.out.println("[INFO] gen: " + gen);
            }

            List<Cromossome> newPop = new ArrayList<>();

            // ELITISMO (top 1 sobrevive)
            population.sort(Comparator.comparingDouble(c -> c.getFitness()));
            newPop.add(population.get(0));

            while (newPop.size() < popSize) {

                Cromossome p1 = tournamentSelection(population, tournamentSize);
                Cromossome p2 = tournamentSelection(population, tournamentSize);

                Cromossome child = crossover(p1, p2);

                mutate(child, mutationRate);

                child.setFitness(evaluator.evaluate(child));

                newPop.add(child);
            }

            population = newPop;
        }

        return population.stream()
                .min(Comparator.comparingDouble(c -> c.getFitness()))
                .orElseThrow();
    }

    /**
     * Gera um cromossomo aleatório inicial para compor a população.
     * Seleciona arestas aleatoriamente e remove conexões sucessivamente caso o
     * custo total de construção ultrapasse o limite do orçamento.
     * 
     * @return Um novo cromossomo válido dentro das restrições de orçamento.
     */
    public Cromossome generateRandomCromossome() {
        Set<Edge> ferrovias = new HashSet<>();

        for (Edge e : allEdges) {
            if (random.nextDouble() < 0.3) {
                ferrovias.add(e);
            }
        }

        Cromossome c = new Cromossome(ferrovias);

        while (!evaluator.validConstructionCost(c) && !c.getFerrovias().isEmpty()) {
            Edge edge = c.getFerrovias().iterator().next();
            c.getFerrovias().remove(edge);
        }

        return c;
    }

    /**
     * Cria a população inicial do algoritmo, gerando indivíduos aleatórios e
     * calculando seus fitness iniciais.
     *
     * @param size Quantidade de indivíduos na população inicial.
     * @return Uma lista de cromossomos prontos para a primeira geração.
     */
    private List<Cromossome> initPopulation(int size) {
        List<Cromossome> pop = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            Cromossome c = generateRandomCromossome();
            c.setFitness(evaluator.evaluate(c));
            pop.add(c);
        }

        return pop;
    }

    /**
     * Realiza a seleção de um indivíduo da população através do método de torneio.
     * Seleciona um grupo aleatório de cromossomos e retorna aquele que possuir o
     * melhor fitness.
     * 
     * @param pop            A população atual.
     * @param tournamentSize O número de competidores em cada torneio.
     * @return O cromossomo vencedor do torneio.
     */
    private Cromossome tournamentSelection(List<Cromossome> pop, int tournamentSize) {

        Cromossome best = null;

        for (int i = 0; i < tournamentSize; i++) {
            Cromossome c = pop.get(random.nextInt(pop.size()));

            if (best == null || c.getFitness() < best.getFitness()) {
                best = c;
            }
        }

        return best;
    }

    /**
     * Realiza a combinação genética (crossover) entre dois pais para gerar um novo
     * descendente.
     * Para cada aresta possível, decide aleatoriamente se herdará a característica
     * (presença ou ausência de ferrovia) do pai 1 ou do pai 2.
     * 
     * @param p1 O primeiro cromossomo pai.
     * @param p2 O segundo cromossomo pai.
     * @return Um novo cromossomo "filho" contendo uma mistura das características
     *         dos pais.
     */
    Cromossome crossover(Cromossome p1, Cromossome p2) {
        Set<Edge> childEdges = new HashSet<>();

        for (Edge e : allEdges) {
            boolean fromP1 = p1.getFerrovias().contains(e);
            boolean fromP2 = p2.getFerrovias().contains(e);

            if (random.nextBoolean()) {
                if (fromP1)
                    childEdges.add(e);
            } else {
                if (fromP2)
                    childEdges.add(e);
            }
        }

        return new Cromossome(childEdges);
    }

    /**
     * Aplica alterações aleatórias em um cromossomo com base na taxa de mutação.
     * Caso uma mutação resulte em uma violação do orçamento, a alteração é
     * revertida (rollback)
     * para garantir a viabilidade do indivíduo.
     * 
     * @param c O cromossomo a ser submetido ao processo de mutação.
     * @param mutationRate A probabilidade de inverter o estado (adicionar/remover)
     *                     de cada aresta.
     */
    void mutate(Cromossome c, double mutationRate) {

        for (Edge e : allEdges) {

            if (random.nextDouble() < mutationRate) {

                // Guarda o estado anterior
                boolean removed = c.getFerrovias().remove(e);
                if (!removed) {
                    c.getFerrovias().add(e);
                }

                // ROLLBACK: Se a mutação estourou o budget, desfazemos
                if (!evaluator.validConstructionCost(c)) {

                    // Desfaz a mutação
                    if (!removed) {
                        c.getFerrovias().remove(e); // Remove o que tinha adicionado
                    } else {
                        c.getFerrovias().add(e); // Adiciona o que tinha removido
                    }
                }
            }

        }
    }
}
