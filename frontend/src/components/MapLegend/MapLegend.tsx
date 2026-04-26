import type { MeshMode } from "../../types/mesh";
import "./MapLegend.css";

type MapLegendProps = {
  meshMode: MeshMode;
  hasActiveRoute: boolean;
  isRouteDetailsOpen: boolean;
};

function getMeshLabel(meshMode: MeshMode): string {
  if (meshMode === "KRUSKAL") {
    return "Malha Kruskal";
  }

  if (meshMode === "GENETIC") {
    return "Malha genética";
  }

  return "Malha completa";
}

export function MapLegend({
  meshMode,
  hasActiveRoute,
  isRouteDetailsOpen,
}: MapLegendProps) {
  return (
    <aside
      className={
        isRouteDetailsOpen
          ? "map-legend map-legend--raised"
          : "map-legend"
      }
      aria-label="Legenda do mapa"
    >
      <header className="map-legend__header">
        <span>Legenda</span>
      </header>

      <div className="map-legend__items">
        <div className="map-legend__item">
          <span
            className={`map-legend__line map-legend__line--${meshMode.toLowerCase()}`}
          />
          <span>{getMeshLabel(meshMode)}</span>
        </div>

        <div className="map-legend__item">
          <span className="map-legend__dot" />
          <span>Capital</span>
        </div>

        {hasActiveRoute && (
          <>
            <div className="map-legend__item">
              <span className="map-legend__line map-legend__line--route-road" />
              <span>Rota rodoviária</span>
            </div>

            <div className="map-legend__item">
              <span className="map-legend__line map-legend__line--route-railway" />
              <span>Rota ferroviária</span>
            </div>

            <div className="map-legend__item">
              <span className="map-legend__dot map-legend__dot--destination" />
              <span>Destino</span>
            </div>
          </>
        )}
      </div>
    </aside>
  );
}