export type MeshEdge = {
  from: string;
  to: string;
};

export function parseEdge(edge: string): MeshEdge {
  const [from, to] = edge.split("-");

  if (!from || !to) {
    throw new Error(`Invalid edge format: ${edge}`);
  }

  return { from, to };
}

export function parseEdges(edges: string[]): MeshEdge[] {
  return edges.map(parseEdge);
}