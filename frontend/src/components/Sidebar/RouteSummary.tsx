import type { RouteResponseApi } from "../../types/api";
import { formatCurrency, formatDistance } from "../../utils/format";

type RouteSummaryProps = {
  route: RouteResponseApi | null;
  onExpandDetails: () => void;
};

function getRouteModeLabel(mode: RouteResponseApi["mode"]): string {
  if (mode === "kruskal-railways") {
    return "Kruskal";
  }

  if (mode === "genetic-railways") {
    return "Genetics";
  }

  return "Rodoviária";
}

export function RouteSummary({ route, onExpandDetails }: RouteSummaryProps) {
  if (!route) {
    return (
      <section className="sidebar-section route-summary route-summary--empty">
        <span className="sidebar-section__label">Resumo da viagem</span>

        <p>
          Nenhuma rota calculada ainda. Selecione origem e destino para visualizar
          os dados da viagem.
        </p>
      </section>
    );
  }

  return (
    <section className="sidebar-section route-summary">
      <div className="sidebar-section__header">
        <span className="sidebar-section__label">Resumo da viagem</span>
        <span className="sidebar-section__badge">{getRouteModeLabel(route.mode)}</span>
      </div>

      <div className="route-summary__title">
        <strong>{route.originName}</strong>
        <span>→</span>
        <strong>{route.destinationName}</strong>
      </div>

      <div className="route-summary__metrics">
        <div className="route-summary__metric">
          <span>Distância</span>
          <strong>{formatDistance(route.totalDistanceKm)}</strong>
        </div>

        <div className="route-summary__metric">
          <span>Custo total</span>
          <strong>{formatCurrency(route.totalCostBrl)}</strong>
        </div>

        <div className="route-summary__metric">
          <span>Transbordos</span>
          <strong>{route.totalTransfers}</strong>
        </div>

        <div className="route-summary__metric">
          <span>Custo transbordo</span>
          <strong>{formatCurrency(route.totalTransferCostBrl)}</strong>
        </div>
      </div>

      <button
        type="button"
        className="sidebar-button"
        onClick={onExpandDetails}
      >
        Ver detalhes dos trechos
      </button>
    </section>
  );
}