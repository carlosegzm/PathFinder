import { CAPITAL_COORDINATES } from "../data/capitalCoordinates";
import type { CapitalCoordinate } from "../data/capitalCoordinates";

const capitalByUf = new Map(
  CAPITAL_COORDINATES.map((capital) => [capital.uf, capital])
);

export function getCapitalByUf(uf: string): CapitalCoordinate | null {
  return capitalByUf.get(uf) ?? null;
}