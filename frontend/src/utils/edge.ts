import type { MeshEdge } from "../types/mesh";

export function parseEdgeCode(edgeCode: string): MeshEdge {
  const [from, to] = edgeCode.split("-");

  if (!from || !to) {
    throw new Error(`Invalid edge format: ${edgeCode}`);
  }

  return { from, to };
}

export function parseEdgeCodes(edgeCodes: string[]): MeshEdge[] {
  return edgeCodes.map(parseEdgeCode);
}