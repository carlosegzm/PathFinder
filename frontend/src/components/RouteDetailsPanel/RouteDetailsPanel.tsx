import type { RouteResponseApi } from "../../types/api";
import { RouteSegmentsList } from "./RouteSegmentsList";
import "./RouteDetailsPanel.css";

type RouteDetailsPanelProps = {
  route: RouteResponseApi | null;
  isOpen: boolean;
  isSidebarCollapsed: boolean;
  onClose: () => void;
};

export function RouteDetailsPanel({
  route,
  isOpen,
  isSidebarCollapsed,
  onClose,
}: RouteDetailsPanelProps) {
  if (!isOpen || !route) {
    return null;
  }

  return (
    <section
      className={
        isSidebarCollapsed
          ? "route-details-panel route-details-panel--sidebar-collapsed"
          : "route-details-panel"
      }
    >
      <header className="route-details-panel__header">
        <div>
          <span>Detalhes da rota</span>
          <h2>
            {route.originName} → {route.destinationName}
          </h2>
        </div>

        <button
          type="button"
          className="route-details-panel__close"
          onClick={onClose}
          aria-label="Fechar detalhes da rota"
        >
          ×
        </button>
      </header>

      <RouteSegmentsList segments={route.segments} />
    </section>
  );
}
