import type { GeneticResponseApi } from "../types/api";
import { STATIC_GENETIC_RESULT } from "../data/staticResults";

const GENETIC_CACHE_KEY = "pathfinder:genetic-result";

export function loadCachedGeneticResult(): GeneticResponseApi {
  const cached = localStorage.getItem(GENETIC_CACHE_KEY);

  if (!cached) {
    return STATIC_GENETIC_RESULT;
  }

  try {
    return JSON.parse(cached) as GeneticResponseApi;
  } catch {
    localStorage.removeItem(GENETIC_CACHE_KEY);
    return STATIC_GENETIC_RESULT;
  }
}

export function saveCachedGeneticResult(result: GeneticResponseApi): void {
  localStorage.setItem(GENETIC_CACHE_KEY, JSON.stringify(result));
}

export function clearCachedGeneticResult(): void {
  localStorage.removeItem(GENETIC_CACHE_KEY);
}