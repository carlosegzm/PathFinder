import type { GeneticResponseApi } from "../../types/api";
import { formatCurrency } from "../../utils/format";

type GeneticPanelProps = {
  result: GeneticResponseApi;
  isRunning: boolean;
  error: string | null;
  onRunAgain: () => void;
  onReset: () => void;
};

export function GeneticPanel({
  result,
  isRunning,
  error,
  onRunAgain,
  onReset,
}: GeneticPanelProps) {
  return (
    <section className="sidebar-section">
      <div className="sidebar-section__header">
        <span className="sidebar-section__label">Algoritmo Genético</span>

        {isRunning && <span className="sidebar-section__badge">Executando...</span>}
      </div>

      <div className="genetic-panel__metrics">
        <div className="genetic-panel__metric">
          <span>Custo transporte</span>
          <strong>{formatCurrency(result.totalTransportCost)}</strong>
        </div>

        <div className="genetic-panel__metric">
          <span>Custo construção</span>
          <strong>{formatCurrency(result.constructionCost)}</strong>
        </div>

        <div className="genetic-panel__metric">
          <span>Orçamento</span>
          <strong>{formatCurrency(result.budgetLimit)}</strong>
        </div>

        <div className="genetic-panel__metric">
          <span>Ferrovias</span>
          <strong>{result.selectedRailways.length}</strong>
        </div>
      </div>

      {error && <p className="genetic-panel__error">{error}</p>}

      <div className="genetic-panel__actions">
        <button
          type="button"
          className="sidebar-button sidebar-button--primary"
          disabled={isRunning}
          onClick={onRunAgain}
        >
          {isRunning ? "Rodando AG..." : "Rodar AG novamente"}
        </button>

        <button
          type="button"
          className="sidebar-button"
          disabled={isRunning}
          onClick={onReset}
        >
          Restaurar inicial
        </button>
      </div>
    </section>
  );
}