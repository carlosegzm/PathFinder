import { useEffect, useRef, useState } from "react";
import * as d3 from "d3";
import { feature } from "topojson-client";
import type { FeatureCollection, Geometry } from "geojson";
import type { Objects, Topology } from "topojson-specification";

import { CAPITAL_COORDINATES } from "../../data/capitalCoordinates";
import { getCapitalByUf } from "../../utils/capitalLookup";
import type { MeshEdge, MeshMode } from "../../types/mesh";
import type { RouteResponseApi, RouteSegmentApi } from "../../types/api";

import "./MapView.css";

type MapLoadState = "idle" | "loading" | "success" | "error";

type MapViewProps = {
  meshMode: MeshMode;
  meshEdges: MeshEdge[];
  activeRoute: RouteResponseApi | null;
};

function getRouteSegmentClass(segment: RouteSegmentApi): string {
  const baseClass = "map-view__route-segment";

  if (segment.transportMode === "RAILWAY") {
    return `${baseClass} ${baseClass}--railway`;
  }

  return `${baseClass} ${baseClass}--road`;
}

export function MapView({ meshMode, meshEdges, activeRoute }: MapViewProps) {
  const svgRef = useRef<SVGSVGElement | null>(null);
  const containerRef = useRef<HTMLDivElement | null>(null);

  const [loadState, setLoadState] = useState<MapLoadState>("idle");
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;

    async function renderMap() {
      const svgElement = svgRef.current;
      const containerElement = containerRef.current;

      if (!svgElement || !containerElement) {
        return;
      }

      try {
        setLoadState("loading");
        setErrorMessage(null);

        const response = await fetch("/maps/br-states.json");

        if (!response.ok) {
          throw new Error(`Failed to load map file: ${response.status}`);
        }

        const topology = (await response.json()) as Topology<Objects>;
        const objectKey = Object.keys(topology.objects)[0];

        if (!objectKey) {
          throw new Error("TopoJSON does not contain any object.");
        }

        const brazilStates = feature(
          topology,
          topology.objects[objectKey],
        ) as FeatureCollection<Geometry>;

        if (!isMounted) {
          return;
        }

        const width = containerElement.clientWidth;
        const height = containerElement.clientHeight;

        const svg = d3.select(svgElement);
        svg.selectAll("*").remove();

        svg.attr("viewBox", `0 0 ${width} ${height}`);

        const rootGroup = svg.append("g").attr("class", "map-view__root");

        const padding = 48;

        const projection = d3.geoMercator().fitExtent(
          [
            [padding, padding],
            [width - padding, height - padding],
          ],
          brazilStates,
        );

        const pathGenerator = d3.geoPath(projection);

        function projectCapital(uf: string): [number, number] | null {
          const capital = getCapitalByUf(uf);

          if (!capital) {
            return null;
          }

          return projection([capital.lon, capital.lat]) ?? null;
        }

        rootGroup
          .append("g")
          .attr("class", "map-view__states")
          .selectAll("path")
          .data(brazilStates.features)
          .join("path")
          .attr("class", "map-view__state")
          .attr("d", pathGenerator);

        const visibleMeshEdges = meshEdges;

        rootGroup
          .append("g")
          .attr(
            "class",
            `map-view__mesh map-view__mesh--${meshMode.toLowerCase()}`,
          )
          .selectAll("line")
          .data(visibleMeshEdges)
          .join("line")
          .attr("class", "map-view__mesh-edge")
          .attr("x1", (edge) => projectCapital(edge.from)?.[0] ?? 0)
          .attr("y1", (edge) => projectCapital(edge.from)?.[1] ?? 0)
          .attr("x2", (edge) => projectCapital(edge.to)?.[0] ?? 0)
          .attr("y2", (edge) => projectCapital(edge.to)?.[1] ?? 0);

        if (activeRoute) {
          rootGroup
            .append("g")
            .attr("class", "map-view__active-route")
            .selectAll("line")
            .data(activeRoute.segments)
            .join("line")
            .attr("class", getRouteSegmentClass)
            .attr("x1", (segment) => projectCapital(segment.fromId)?.[0] ?? 0)
            .attr("y1", (segment) => projectCapital(segment.fromId)?.[1] ?? 0)
            .attr("x2", (segment) => projectCapital(segment.toId)?.[0] ?? 0)
            .attr("y2", (segment) => projectCapital(segment.toId)?.[1] ?? 0);

          rootGroup
            .append("g")
            .attr("class", "map-view__route-points")
            .selectAll("circle")
            .data(activeRoute.segments)
            .join("circle")
            .attr("class", "map-view__route-point")
            .attr("cx", (segment) => projectCapital(segment.fromId)?.[0] ?? 0)
            .attr("cy", (segment) => projectCapital(segment.fromId)?.[1] ?? 0)
            .attr("r", 5);

          const lastSegment = activeRoute.segments.at(-1);

          if (lastSegment) {
            const destinationPoint = projectCapital(lastSegment.toId);

            if (destinationPoint) {
              rootGroup
                .append("circle")
                .attr(
                  "class",
                  "map-view__route-point map-view__route-point--destination",
                )
                .attr("cx", destinationPoint[0])
                .attr("cy", destinationPoint[1])
                .attr("r", 6);
            }
          }
        }
        const capitalNodes = rootGroup
          .append("g")
          .attr("class", "map-view__capitals")
          .selectAll("g")
          .data(CAPITAL_COORDINATES)
          .join("g")
          .attr("class", "map-view__capital-node")
          .attr("transform", (capital) => {
            const point = projection([capital.lon, capital.lat]);

            if (!point) {
              return "translate(0, 0)";
            }

            return `translate(${point[0]}, ${point[1]})`;
          });

        capitalNodes
          .append("circle")
          .attr("class", "map-view__capital-dot")
          .attr("r", 4);

        capitalNodes
          .append("text")
          .attr("class", "map-view__capital-label")
          .attr("x", 7)
          .attr("y", 4)
          .text((capital) => capital.uf);

        capitalNodes
          .append("title")
          .text((capital) => `${capital.name} (${capital.uf})`);

        const zoom = d3
          .zoom<SVGSVGElement, unknown>()
          .scaleExtent([1, 8])
          .on("zoom", (event) => {
            rootGroup.attr("transform", event.transform.toString());
          });

        svg.call(zoom);

        setLoadState("success");
      } catch (error) {
        if (!isMounted) {
          return;
        }

        const message =
          error instanceof Error
            ? error.message
            : "Unknown error while loading map.";

        setLoadState("error");
        setErrorMessage(message);
      }
    }

    renderMap();

    function handleResize() {
      renderMap();
    }

    window.addEventListener("resize", handleResize);

    return () => {
      isMounted = false;
      window.removeEventListener("resize", handleResize);
    };
  }, [meshMode, meshEdges, activeRoute]);

  return (
    <div ref={containerRef} className="map-view">
      <svg
        ref={svgRef}
        className="map-view__svg"
        role="img"
        aria-label="Mapa do Brasil"
      />

      {loadState === "loading" && (
        <div className="map-view__status">Carregando mapa...</div>
      )}

      {loadState === "error" && (
        <div className="map-view__status map-view__status--error">
          Não foi possível carregar o mapa.
          {errorMessage && <span>{errorMessage}</span>}
        </div>
      )}
    </div>
  );
}
