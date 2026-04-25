import { apiClient } from "./apiClient";
import type {
  GeneticRequestApi,
  GeneticResponseApi,
  KruskalResponseApi,
  RouteResponseApi,
} from "../types/api";

export async function getRoadRoute(origin: string, destination: string) {
  const response = await apiClient.post<RouteResponseApi>("/astar/road", {
    origin,
    destination,
  });

  return response.data;
}

export async function getKruskal() {
  const response = await apiClient.get<KruskalResponseApi>("/kruskal");
  return response.data;
}

export async function runGenetic(payload: GeneticRequestApi) {
  const response = await apiClient.post<GeneticResponseApi>("/genetic/run", payload);
  return response.data;
}

export async function getKruskalRoute(
  origin: string,
  destination: string,
  railwayNetwork: string[]
) {
  const response = await apiClient.post<RouteResponseApi>("/astar/kruskal", {
    origin,
    destination,
    railwayNetwork,
  });

  return response.data;
}

export async function getGeneticRoute(
  origin: string,
  destination: string,
  railwayEdges: string[]
) {
  const response = await apiClient.post<RouteResponseApi>("/astar/genetic", {
    origin,
    destination,
    railwayEdges,
  });

  return response.data;
}