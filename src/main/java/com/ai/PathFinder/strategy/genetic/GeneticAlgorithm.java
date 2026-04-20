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

    public GeneticAlgorithm(List<Edge> allEdges, FitnessEvaluator evaluator) {
        this.allEdges = allEdges;
        this.evaluator = evaluator;
    }

    public Cromossome run(int popSize, int generations) {

        List<Cromossome> population = initPopulation(popSize);

        for (int gen = 0; gen < generations; gen++) {

            // log báico pra acompanhar o progresso do algoritmo
            if(gen % 10 == 0){
                System.out.println("[INFO] gen: " + gen);
            }

            List<Cromossome> newPop = new ArrayList<>();

            // ELITISMO (top 1 sobrevive)
            population.sort(Comparator.comparingDouble(c -> c.getFitness()));
            newPop.add(population.get(0));

            while (newPop.size() < popSize) {

                Cromossome p1 = tournamentSelection(population);
                Cromossome p2 = tournamentSelection(population);

                Cromossome child = crossover(p1, p2);

                mutate(child, 0.02);

                child.setFitness(evaluator.evaluate(child));

                newPop.add(child);
            }

            population = newPop;

            System.out.println("Gen " + gen + " best: " + population.get(0).getFitness());
        }

        return population.stream()
                .min(Comparator.comparingDouble(c -> c.getFitness()))
                .orElseThrow();
    }

    public Cromossome generateRandomCromossome() {
        Set<Edge> ferrovias = new HashSet<>();

        for (Edge e : allEdges) {
            if (random.nextBoolean()) {
                ferrovias.add(e);
            }
        }

        return new Cromossome(ferrovias);
    }

    List<Cromossome> initPopulation(int size) {
        List<Cromossome> pop = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            Cromossome c = generateRandomCromossome();
            c.setFitness(evaluator.evaluate(c));
            pop.add(c);
        }

        return pop;
    }

    Cromossome tournamentSelection(List<Cromossome> pop) {
        int k = 3; // tamanho do torneio

        Cromossome best = null;

        for (int i = 0; i < k; i++) {
            Cromossome c = pop.get(random.nextInt(pop.size()));

            if (best == null || c.getFitness() < best.getFitness()) {
                best = c;
            }
        }

        return best;
    }

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

    void mutate(Cromossome c, double mutationRate) {

        for (Edge e : allEdges) {

            if (random.nextDouble() < mutationRate) {

                if (c.getFerrovias().contains(e)) {
                    c.getFerrovias().remove(e);
                } else {
                    c.getFerrovias().add(e);
                }
            }
        }
    }
}
