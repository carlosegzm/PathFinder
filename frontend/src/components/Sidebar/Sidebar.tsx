import type { GeneticResponseApi, RouteResponseApi } from "../../types/api";
import type { MeshMode } from "../../types/mesh";
import type { RouteMode } from "../../types/route";
import { GeneticPanel } from "./GeneticPanel";
import { MeshControls } from "./MeshControls";
import { RouteControls } from "./RouteControls";
import { RouteSummary } from "./RouteSummary";
import { ChevronLeft, ChevronRight } from "lucide-react";
import { appConfig } from "../../config/appConfig";
import "./Sidebar.css";

type SidebarProps = {
  meshMode: MeshMode;
  geneticResult: GeneticResponseApi;
  isRunningGenetic: boolean;
  geneticError: string | null;

  originUf: string;
  destinationUf: string;
  routeMode: RouteMode;
  activeRoute: RouteResponseApi | null;
  isCalculatingRoute: boolean;
  routeError: string | null;

  isCollapsed: boolean;

  onMeshModeChange: (mode: MeshMode) => void;
  onRunGenetic: () => void;
  onResetGenetic: () => void;

  onOriginChange: (uf: string) => void;
  onDestinationChange: (uf: string) => void;
  onRouteModeChange: (mode: RouteMode) => void;
  onCalculateRoute: () => void;
  onExpandRouteDetails: () => void;

  onToggleCollapse: () => void;
};

export function Sidebar({
  meshMode,
  geneticResult,
  isRunningGenetic,
  geneticError,

  originUf,
  destinationUf,
  routeMode,
  activeRoute,
  isCalculatingRoute,
  routeError,

  isCollapsed,

  onMeshModeChange,
  onRunGenetic,
  onResetGenetic,

  onOriginChange,
  onDestinationChange,
  onRouteModeChange,
  onCalculateRoute,
  onExpandRouteDetails,

  onToggleCollapse,
}: SidebarProps) {
  return (
    <aside className={isCollapsed ? "sidebar sidebar--collapsed" : "sidebar"}>
      {appConfig.useMockRoute && (
        <p className="sidebar-dev-warning">Mock de rota ativo</p>
      )}
      <button
        type="button"
        className="sidebar__collapse-button"
        onClick={onToggleCollapse}
        aria-label={
          isCollapsed ? "Expandir menu lateral" : "Colapsar menu lateral"
        }
        title={isCollapsed ? "Expandir" : "Colapsar"}
      >
        {isCollapsed ? <ChevronRight size={18} /> : <ChevronLeft size={18} />}
      </button>

      {isCollapsed && (
        <div className="sidebar__collapsed-brand" aria-hidden="true">
          <span>PF</span>
        </div>
      )}

      {!isCollapsed && (
        <>
          <header className="sidebar__header">
            <h1>PathFinder</h1>
            <p>Controle de rotas e malhas ferroviárias.</p>
          </header>

          <div className="sidebar__content">
            <MeshControls value={meshMode} onChange={onMeshModeChange} />

            <RouteControls
              originUf={originUf}
              destinationUf={destinationUf}
              routeMode={routeMode}
              isCalculatingRoute={isCalculatingRoute}
              routeError={routeError}
              onOriginChange={onOriginChange}
              onDestinationChange={onDestinationChange}
              onRouteModeChange={onRouteModeChange}
              onCalculateRoute={onCalculateRoute}
            />

            <RouteSummary
              route={activeRoute}
              onExpandDetails={onExpandRouteDetails}
            />

            <GeneticPanel
              result={geneticResult}
              isRunning={isRunningGenetic}
              error={geneticError}
              onRunAgain={onRunGenetic}
              onReset={onResetGenetic}
            />
          </div>
        </>
      )}
    </aside>
  );
}
