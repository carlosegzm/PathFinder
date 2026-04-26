import { useMemo, useState } from "react";

import { FULL_MESH } from "../data/fullMesh";
import {
  DEFAULT_GENETIC_PARAMS,
  STATIC_GENETIC_RESULT,
  STATIC_KRUSKAL_RESULT,
} from "../data/staticResults";
import {
  getGeneticRoute,
  getKruskalRoute,
  getRoadRoute,
  runGenetic,
} from "../services/pathfinderService";
import type { GeneticResponseApi, RouteResponseApi } from "../types/api";
import type { MeshEdge, MeshMode } from "../types/mesh";
import type { RouteMode } from "../types/route";
import { parseEdgeCodes } from "../utils/edge";
import {
  clearCachedGeneticResult,
  loadCachedGeneticResult,
  saveCachedGeneticResult,
} from "../utils/geneticCache";

import { MOCK_ROUTE } from "../data/mockRoute";
import { appConfig } from "../config/appConfig";

import { getErrorMessage } from "../utils/error";

function getMeshEdgesByMode(
  meshMode: MeshMode,
  geneticResult: GeneticResponseApi,
): MeshEdge[] {
  if (meshMode === "KRUSKAL") {
    return parseEdgeCodes(STATIC_KRUSKAL_RESULT.railwayNetwork);
  }

  if (meshMode === "GENETIC") {
    return parseEdgeCodes(geneticResult.selectedRailways);
  }

  return FULL_MESH;
}

export function usePathfinder() {
  const [isSidebarCollapsed, setIsSidebarCollapsed] = useState(false);

  const [meshMode, setMeshMode] = useState<MeshMode>("FULL");

  const [geneticResult, setGeneticResult] = useState<GeneticResponseApi>(() =>
    loadCachedGeneticResult(),
  );

  const [isRunningGenetic, setIsRunningGenetic] = useState(false);
  const [geneticError, setGeneticError] = useState<string | null>(null);

  const [originUf, setOriginUf] = useState("");
  const [destinationUf, setDestinationUf] = useState("");
  const [routeMode, setRouteMode] = useState<RouteMode>("ROAD");

  const [activeRoute, setActiveRoute] = useState<RouteResponseApi | null>(
    appConfig.useMockRoute ? MOCK_ROUTE : null,
  );

  const [isCalculatingRoute, setIsCalculatingRoute] = useState(false);
  const [routeError, setRouteError] = useState<string | null>(null);

  const [isRouteDetailsOpen, setIsRouteDetailsOpen] = useState(false);

  const visibleMeshEdges = useMemo(
    () => getMeshEdgesByMode(meshMode, geneticResult),
    [meshMode, geneticResult],
  );

  function toggleSidebar() {
    setIsSidebarCollapsed((current) => !current);
  }

  function handleOriginChange(uf: string) {
    setOriginUf(uf);
    setRouteError(null);
    setIsRouteDetailsOpen(false);
  }

  function handleDestinationChange(uf: string) {
    setDestinationUf(uf);
    setRouteError(null);
    setIsRouteDetailsOpen(false);
  }

  function handleRouteModeChange(mode: RouteMode) {
    setRouteMode(mode);
    setRouteError(null);
    setIsRouteDetailsOpen(false);
  }

  async function handleRunGenetic() {
    try {
      setIsRunningGenetic(true);
      setGeneticError(null);

      const nextResult = await runGenetic({
        budgetLimit: STATIC_KRUSKAL_RESULT.availableBudgetForGenetics,
        ...DEFAULT_GENETIC_PARAMS,
      });

      setGeneticResult(nextResult);
      saveCachedGeneticResult(nextResult);

      if (activeRoute?.mode === "genetic-railways") {
        setActiveRoute(null);
        setIsRouteDetailsOpen(false);
      }
    } catch (error) {
      setGeneticError(
        getErrorMessage(
          error,
          "Não foi possível executar o algoritmo genético.",
        ),
      );
    } finally {
      setIsRunningGenetic(false);
    }
  }

  function handleResetGenetic() {
    setGeneticResult(STATIC_GENETIC_RESULT);
    clearCachedGeneticResult();
    setGeneticError(null);

    if (activeRoute?.mode === "genetic-railways") {
      setActiveRoute(null);
      setIsRouteDetailsOpen(false);
    }
  }

  async function handleCalculateRoute() {
    if (!originUf || !destinationUf) {
      setRouteError("Selecione origem e destino.");
      return;
    }

    if (originUf === destinationUf) {
      setRouteError("Origem e destino precisam ser diferentes.");
      return;
    }

    try {
      setIsCalculatingRoute(true);
      setRouteError(null);
      setIsRouteDetailsOpen(false);

      let route: RouteResponseApi;

      if (routeMode === "KRUSKAL") {
        route = await getKruskalRoute(
          originUf,
          destinationUf,
          STATIC_KRUSKAL_RESULT.railwayNetwork,
        );

        setMeshMode("KRUSKAL");
      } else if (routeMode === "GENETIC") {
        route = await getGeneticRoute(
          originUf,
          destinationUf,
          geneticResult.selectedRailways,
        );

        setMeshMode("GENETIC");
      } else {
        route = await getRoadRoute(originUf, destinationUf);

        setMeshMode("FULL");
      }

      if (!route.routeFound) {
        setActiveRoute(null);
        setRouteError(
          "Nenhuma rota encontrada para a origem e destino selecionados.",
        );
        return;
      }

      setActiveRoute(route);
    } catch (error) {
      setGeneticError(
        getErrorMessage(error, "Não foi possível calcular a rota."),
      );
    } finally {
      setIsCalculatingRoute(false);
    }
  }

  return {
    meshMode,
    setMeshMode,
    visibleMeshEdges,

    geneticResult,
    isRunningGenetic,
    geneticError,
    handleRunGenetic,
    handleResetGenetic,

    originUf,
    destinationUf,
    routeMode,
    activeRoute,
    isCalculatingRoute,
    routeError,

    handleOriginChange,
    handleDestinationChange,
    handleRouteModeChange,
    handleCalculateRoute,

    isRouteDetailsOpen,
    setIsRouteDetailsOpen,

    isSidebarCollapsed,
    toggleSidebar,
  };
}
