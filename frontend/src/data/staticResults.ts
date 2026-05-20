import type { GeneticResponseApi, KruskalResponseApi } from "../types/api";

export const STATIC_KRUSKAL_RESULT: KruskalResponseApi = {
  "totalDistanceKm": 14429.0,
  "totalConstructionCost": 28858000000.000,
  "availableBudgetForGenetics": 17314800000.00000,
  "railwayNetwork": [
    "MT-RO",
    "AL-SE",
    "PR-SP",
    "PI-TO",
    "MA-PI",
    "AM-RO",
    "DF-GO",
    "MS-MT",
    "GO-TO",
    "MA-PA",
    "CE-PI",
    "MG-RJ",
    "CE-RN",
    "DF-MG",
    "AL-PE",
    "BA-SE",
    "GO-MS",
    "ES-MG",
    "PB-RN",
    "RS-SC",
    "PB-PE",
    "PR-SC",
    "AP-PA",
    "AM-RR",
    "RJ-SP",
    "AC-RO"
  ]
}

export const STATIC_GENETIC_RESULT: GeneticResponseApi = {
	"totalTransportCost": 1.1022677E7,
	"constructionCost": 1.7158E10,
	"budgetLimit": 1.73148E10,
	"selectedRailways": [
		"PR-SP",
		"MG-RJ",
		"RS-SC",
		"CE-RN",
		"DF-GO",
		"MA-PI",
		"RJ-SP",
		"GO-MT",
		"PB-PE",
		"BA-PE",
		"BA-MG",
		"DF-MG",
		"PB-RN",
		"MA-PA",
		"CE-PI",
		"PR-SC"
	]
}

export const DEFAULT_GENETIC_PARAMS = {
  popSize: 200,
  generations: 100,
  mutationRate: 0.02,
  tournamentSize: 3,
};