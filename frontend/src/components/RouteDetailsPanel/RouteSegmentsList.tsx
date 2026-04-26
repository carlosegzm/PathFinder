import type { RouteSegmentApi } from "../../types/api";
import { formatCurrency, formatDistance } from "../../utils/format";

type RouteSegmentsListProps = {
  segments: RouteSegmentApi[];
};

function getTransportModeLabel(mode: RouteSegmentApi["transportMode"]): string {
  return mode === "RAILWAY" ? "Ferrovia" : "Rodovia";
}

export function RouteSegmentsList({ segments }: RouteSegmentsListProps) {
  if (segments.length === 0) {
    return <p className="route-segments__empty">Nenhum trecho disponível.</p>;
  }

  return (
    <div className="route-segments">
      {segments.map((segment, index) => (
        <article
          key={`${segment.fromId}-${segment.toId}-${index}`}
          className="route-segments__item"
        >
          <div className="route-segments__header">
            <strong>
              {segment.fromName} ({segment.fromId}) → {segment.toName} ({segment.toId})
            </strong>

            <span
              className={
                segment.transportMode === "RAILWAY"
                  ? "route-segments__mode route-segments__mode--railway"
                  : "route-segments__mode"
              }
            >
              {getTransportModeLabel(segment.transportMode)}
            </span>
          </div>

          <div className="route-segments__metrics">
            <span>{formatDistance(segment.distanceKm)}</span>
            <span>{formatCurrency(segment.segmentCostBrl)}</span>
            {segment.hasTransfer && <span>Transbordo</span>}
          </div>
        </article>
      ))}
    </div>
  );
}