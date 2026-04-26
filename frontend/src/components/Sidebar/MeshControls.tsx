import type { MeshMode } from "../../types/mesh";

type MeshControlsProps = {
  value: MeshMode;
  onChange: (mode: MeshMode) => void;
};

const MESH_MODES: { value: MeshMode; label: string }[] = [
  { value: "FULL", label: "Full" },
  { value: "KRUSKAL", label: "Kruskal" },
  { value: "GENETIC", label: "Genetics" },
];

export function MeshControls({ value, onChange }: MeshControlsProps) {
  return (
    <section className="sidebar-section">
      <span className="sidebar-section__label">Malha exibida</span>

      <div className="segmented-control">
        {MESH_MODES.map((mode) => (
          <button
            key={mode.value}
            type="button"
            className={
              value === mode.value
                ? "segmented-control__button segmented-control__button--active"
                : "segmented-control__button"
            }
            onClick={() => onChange(mode.value)}
          >
            {mode.label}
          </button>
        ))}
      </div>
    </section>
  );
}