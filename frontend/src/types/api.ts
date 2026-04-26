export type TransportModeApi = "ROAD" | "RAILWAY";

export type RouteSegmentApi = {
  fromId: string;
  fromName: string;
  toId: string;
  toName: string;
  distanceKm: number;
  transportMode: TransportModeApi;
  hasTransfer: boolean;
  segmentCostBrl: number;
};

export type RouteResponseApi = {
  originId: string;
  originName: string;
  destinationId: string;
  destinationName: string;
  totalDistanceKm: number;
  totalCostBrl: number;
  totalTransfers: number;
  totalTransferCostBrl: number;
  mode: "road-only" | "kruskal-railways" | "genetic-railways";
  routeFound: boolean;
  segments: RouteSegmentApi[];
};

export type KruskalResponseApi = {
  totalDistanceKm: number;
  totalConstructionCost: number;
  availableBudgetForGenetics: number;
  railwayNetwork: string[];
};

export type GeneticRequestApi = {
  budgetLimit: number;
  popSize: number;
  generations: number;
  mutationRate: number;
  tournamentSize: number;
};

export type GeneticResponseApi = {
  totalTransportCost: number;
  constructionCost: number;
  budgetLimit: number;
  selectedRailways: string[];
};