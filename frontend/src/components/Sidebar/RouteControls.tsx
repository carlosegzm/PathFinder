import { CAPITALS } from "../../data/capitals";
import type { RouteMode } from "../../types/route";

type RouteControlsProps = {
  originUf: string;
  destinationUf: string;
  routeMode: RouteMode;
  isCalculatingRoute: boolean;
  routeError: string | null;
  onOriginChange: (uf: string) => void;
  onDestinationChange: (uf: string) => void;
  onRouteModeChange: (mode: RouteMode) => void;
  onCalculateRoute: () => void;
};

const ROUTE_MODES: { value: RouteMode; label: string }[] = [
  { value: "ROAD", label: "Road" },
  { value: "KRUSKAL", label: "Kruskal" },
  { value: "GENETIC", label: "Genetics" },
];

export function RouteControls({
  originUf,
  destinationUf,
  routeMode,
  isCalculatingRoute,
  routeError,
  onOriginChange,
  onDestinationChange,
  onRouteModeChange,
  onCalculateRoute,
}: RouteControlsProps) {
  const isInvalidRoute = !originUf || !destinationUf || originUf === destinationUf;

  return (
    <section className="sidebar-section">
      <div className="sidebar-section__header">
        <span className="sidebar-section__label">Rota</span>

        {isCalculatingRoute && (
          <span className="sidebar-section__badge">Calculando...</span>
        )}
      </div>

      <label className="form-field">
        <span>Origem</span>
        <select
          value={originUf}
          disabled={isCalculatingRoute}
          onChange={(event) => onOriginChange(event.target.value)}
        >
          <option value="">Selecione</option>
          {CAPITALS.map((capital) => (
            <option key={capital.uf} value={capital.uf}>
              {capital.name} ({capital.uf})
            </option>
          ))}
        </select>
      </label>

      <label className="form-field">
        <span>Destino</span>
        <select
          value={destinationUf}
          disabled={isCalculatingRoute}
          onChange={(event) => onDestinationChange(event.target.value)}
        >
          <option value="">Selecione</option>
          {CAPITALS.map((capital) => (
            <option key={capital.uf} value={capital.uf}>
              {capital.name} ({capital.uf})
            </option>
          ))}
        </select>
      </label>

      <div className="route-mode-control">
        <span className="route-mode-control__label">Tipo de rota</span>

        <div className="segmented-control">
          {ROUTE_MODES.map((mode) => (
            <button
              key={mode.value}
              type="button"
              disabled={isCalculatingRoute}
              className={
                routeMode === mode.value
                  ? "segmented-control__button segmented-control__button--active"
                  : "segmented-control__button"
              }
              onClick={() => onRouteModeChange(mode.value)}
            >
              {mode.label}
            </button>
          ))}
        </div>
      </div>

      {originUf && destinationUf && originUf === destinationUf && (
        <p className="sidebar-message sidebar-message--warning">
          Origem e destino precisam ser diferentes.
        </p>
      )}

      {routeError && (
        <p className="sidebar-message sidebar-message--error">{routeError}</p>
      )}

      <button
        type="button"
        className="sidebar-button sidebar-button--primary"
        disabled={isCalculatingRoute || isInvalidRoute}
        onClick={onCalculateRoute}
      >
        {isCalculatingRoute ? "Calculando rota..." : "Calcular rota"}
      </button>
    </section>
  );
}