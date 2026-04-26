import { MapView } from "./components/MapView/MapView";
import { Sidebar } from "./components/Sidebar/Sidebar";
import { RouteDetailsPanel } from "./components/RouteDetailsPanel/RouteDetailsPanel";
import { usePathfinder } from "./hooks/usePathfinder";
import { MapLegend } from "./components/MapLegend/MapLegend";
import "./App.css";

export default function App() {
  const pathfinder = usePathfinder();

  return (
    <main className="app">
      <section className="app__map">
        <MapView
          meshMode={pathfinder.meshMode}
          meshEdges={pathfinder.visibleMeshEdges}
          activeRoute={pathfinder.activeRoute}
        />
      </section>

      <Sidebar
        meshMode={pathfinder.meshMode}
        geneticResult={pathfinder.geneticResult}
        isRunningGenetic={pathfinder.isRunningGenetic}
        geneticError={pathfinder.geneticError}
        originUf={pathfinder.originUf}
        destinationUf={pathfinder.destinationUf}
        routeMode={pathfinder.routeMode}
        activeRoute={pathfinder.activeRoute}
        isCalculatingRoute={pathfinder.isCalculatingRoute}
        routeError={pathfinder.routeError}
        isCollapsed={pathfinder.isSidebarCollapsed}
        onMeshModeChange={pathfinder.setMeshMode}
        onRunGenetic={pathfinder.handleRunGenetic}
        onResetGenetic={pathfinder.handleResetGenetic}
        onOriginChange={pathfinder.handleOriginChange}
        onDestinationChange={pathfinder.handleDestinationChange}
        onRouteModeChange={pathfinder.handleRouteModeChange}
        onCalculateRoute={pathfinder.handleCalculateRoute}
        onExpandRouteDetails={() => pathfinder.setIsRouteDetailsOpen(true)}
        onToggleCollapse={pathfinder.toggleSidebar}
      />

      <MapLegend
        meshMode={pathfinder.meshMode}
        hasActiveRoute={Boolean(pathfinder.activeRoute)}
        isRouteDetailsOpen={pathfinder.isRouteDetailsOpen}
      />

      <RouteDetailsPanel
        route={pathfinder.activeRoute}
        isOpen={pathfinder.isRouteDetailsOpen}
        isSidebarCollapsed={pathfinder.isSidebarCollapsed}
        onClose={() => pathfinder.setIsRouteDetailsOpen(false)}
      />
    </main>
  );
}
