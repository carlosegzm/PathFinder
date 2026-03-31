package com.ai.PathFinder.strategy.search;

import com.ai.PathFinder.strategy.search.AStar.AStarResult;
import com.ai.PathFinder.strategy.search.AStar.TransportMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração do A* multimodal.
 *
 * Requer banco populado com o script SQL do projeto.
 * Todos os valores esperados foram calculados manualmente:
 *   - Rodovia:  distância × R$ 5,00
 *   - Ferrovia: distância × R$ 1,20
 *   - Transbordo: + R$ 1.000,00 por troca de modal
 *
 * Como rodar:
 *   ./mvnw test -Dtest=AStarTest
 */
@SpringBootTest
class AStarTest {

    @Autowired
    private AStar aStar;

    @BeforeEach
    void setUp() {
        // Antes de cada teste: grafo sem nenhuma ferrovia (road-only limpo)
        aStar.rebuildGraphWithRailways(Set.of());
    }

    // BLOCO 1 — Road-only: rotas diretas com custo determinístico

    @Test
    @DisplayName("SP → RJ direto: 435km × R$5,00 = R$2.175,00")
    void road_SP_RJ() {
        AStarResult r = aStar.findRoute("SP", "RJ");

        assertTrue(r.found);
        assertEquals("SP", r.route.get(0).getId());
        assertEquals("RJ", r.route.get(1).getId());
        assertEquals(2, r.route.size());
        assertEquals(1, r.edges.size());
        assertEquals(TransportMode.ROAD, r.edges.get(0).mode);
        assertEquals(435, r.edges.get(0).distanceKm);
        assertEquals(2175.00, r.totalCostBrl, 0.01);
    }

    @Test
    @DisplayName("DF → GO direto: 207km × R$5,00 = R$1.035,00")
    void road_DF_GO() {
        AStarResult r = aStar.findRoute("DF", "GO");
        assertTrue(r.found);
        assertEquals(1035.00, r.totalCostBrl, 0.01);
    }

    @Test
    @DisplayName("PB → RN direto: 181km × R$5,00 = R$905,00")
    void road_PB_RN() {
        AStarResult r = aStar.findRoute("PB", "RN");
        assertTrue(r.found);
        assertEquals(905.00, r.totalCostBrl, 0.01);
    }

    @Test
    @DisplayName("AC → RO direto: 509km × R$5,00 = R$2.545,00")
    void road_AC_RO() {
        AStarResult r = aStar.findRoute("AC", "RO");
        assertTrue(r.found);
        assertEquals(2545.00, r.totalCostBrl, 0.01);
    }

    @Test
    @DisplayName("PR → SC direto: 307km × R$5,00 = R$1.535,00")
    void road_PR_SC() {
        AStarResult r = aStar.findRoute("PR", "SC");
        assertTrue(r.found);
        assertEquals(1535.00, r.totalCostBrl, 0.01);
    }

    @Test
    @DisplayName("AL → PE direto: 256km × R$5,00 = R$1.280,00")
    void road_AL_PE() {
        AStarResult r = aStar.findRoute("AL", "PE");
        assertTrue(r.found);
        assertEquals(1280.00, r.totalCostBrl, 0.01);
    }

    @Test
    @DisplayName("MS → PR direto: 362km × R$5,00 = R$1.810,00")
    void road_MS_PR() {
        AStarResult r = aStar.findRoute("MS", "PR");
        assertTrue(r.found);
        assertEquals(1810.00, r.totalCostBrl, 0.01);
    }

    @Test
    @DisplayName("SP → AM multiestado: rota encontrada com custo razoável")
    void road_SP_AM() {
        AStarResult r = aStar.findRoute("SP", "AM");
        assertTrue(r.found);
        assertEquals("SP", r.route.get(0).getId());
        assertEquals("AM", r.route.get(r.route.size() - 1).getId());
        assertTrue(r.route.size() >= 3);
        assertTrue(r.totalCostBrl > 0);
    }

    // BLOCO 2 — Ferrovia: A* deve preferir trem quando é mais barato

    @Test
    @DisplayName("SP → RJ com ferrovia: 435km × R$1,20 = R$522,00")
    void railway_SP_RJ() {
        aStar.rebuildGraphWithRailways(Set.of("SP-RJ", "RJ-SP"));
        AStarResult r = aStar.findRoute("SP", "RJ");

        assertTrue(r.found);
        assertEquals(1, r.edges.size());
        assertEquals(TransportMode.RAILWAY, r.edges.get(0).mode);
        assertEquals(522.00, r.totalCostBrl, 0.01);
    }

    @Test
    @DisplayName("DF → GO com ferrovia: 207km × R$1,20 = R$248,40")
    void railway_DF_GO() {
        aStar.rebuildGraphWithRailways(Set.of("DF-GO", "GO-DF"));
        AStarResult r = aStar.findRoute("DF", "GO");

        assertTrue(r.found);
        assertEquals(TransportMode.RAILWAY, r.edges.get(0).mode);
        assertEquals(248.40, r.totalCostBrl, 0.01);
    }

    @Test
    @DisplayName("SC → RS com ferrovia: 463km × R$1,20 = R$555,60")
    void railway_SC_RS() {
        aStar.rebuildGraphWithRailways(Set.of("SC-RS", "RS-SC"));
        AStarResult r = aStar.findRoute("SC", "RS");

        assertTrue(r.found);
        assertEquals(TransportMode.RAILWAY, r.edges.get(0).mode);
        assertEquals(555.60, r.totalCostBrl, 0.01);
    }

    @Test
    @DisplayName("SP → MG via RJ com 2 ferrovias contínuas: (435+441)km × R$1,20 = R$1.051,20 sem transbordo")
    void railway_SP_MG_via_RJ_continuous() {
        // SP→RJ (ferrovia) + RJ→MG (ferrovia): sem transbordo, tudo trem
        aStar.rebuildGraphWithRailways(Set.of("SP-RJ", "RJ-SP", "RJ-MG", "MG-RJ"));
        AStarResult r = aStar.findRoute("SP", "MG");

        assertTrue(r.found);

        // Sem transbordo: ferrovia contínua
        assertEquals(0, countTransfers(r));

        // (435 + 441) × R$1,20 = R$1.051,20
        // vs rodovia direta SP→MG: 583 × R$5,00 = R$2.915,00
        assertEquals(1051.20, r.totalCostBrl, 0.01);
    }

    // BLOCO 3 — Transbordo: A* deve evitar trocas de modal quando não compensam

    @Test
    @DisplayName("Transbordo não compensa: SP → MG direto (R$2.915) melhor que via RJ com transbordo (R$3.727)")
    void transfer_SP_MG_avoidsMix() {
        // Só SP-RJ tem ferrovia. RJ-MG é só rodovia.
        // Via RJ: 435×1,20 + R$1.000 + 441×5,00 = 522 + 1000 + 2205 = R$3.727,00
        // Direto SP→MG: 583 × R$5,00 = R$2.915,00
        aStar.rebuildGraphWithRailways(Set.of("SP-RJ", "RJ-SP"));
        AStarResult r = aStar.findRoute("SP", "MG");

        assertTrue(r.found);
        assertTrue(r.totalCostBrl <= 2915.00 + 0.01,
                "A* deve preferir rodovia direta (R$2.915) sobre rota com transbordo (R$3.727)");
    }

    @Test
    @DisplayName("Transbordo não compensa: GO → MG direto (R$4.455) melhor que via DF com transbordo (R$4.943,40)")
    void transfer_GO_MG_avoidsMix() {
        // GO→DF ferrovia (207km) + DF→MG rodovia (739km)
        // Custo: 207×1,20 + 1000 + 739×5 = 248,40 + 1000 + 3695 = R$4.943,40
        // GO→MG direto rodovia: 891×5 = R$4.455,00
        aStar.rebuildGraphWithRailways(Set.of("GO-DF", "DF-GO"));
        AStarResult r = aStar.findRoute("GO", "MG");

        assertTrue(r.found);
        assertEquals(4455.00, r.totalCostBrl, 0.01);
    }

    // BLOCO 4 — Casos-limite

    @Test
    @DisplayName("Origem igual ao destino: custo R$0,00 e uma cidade na rota")
    void edge_sameOriginDestination() {
        AStarResult r = aStar.findRoute("SP", "SP");
        assertTrue(r.found);
        assertEquals(0.0, r.totalCostBrl, 0.001);
        assertEquals(1, r.route.size());
        assertEquals(0, r.edges.size());
    }

    @Test
    @DisplayName("Capital inexistente: routeFound = false")
    void edge_invalidCapital() {
        AStarResult r = aStar.findRoute("ZZ", "SP");
        assertFalse(r.found);
    }

    @Test
    @DisplayName("AC → AP: rota longa na região amazônica deve ser encontrada")
    void edge_AC_AP() {
        AStarResult r = aStar.findRoute("AC", "AP");
        assertTrue(r.found);
        assertEquals("AC", r.route.get(0).getId());
        assertEquals("AP", r.route.get(r.route.size() - 1).getId());
    }

    @Test
    @DisplayName("RR → AL: extremos geográficos do Brasil devem ser conectados")
    void edge_RR_AL() {
        AStarResult r = aStar.findRoute("RR", "AL");
        assertTrue(r.found);
        assertTrue(r.route.size() >= 4);
    }

    // BLOCO 5 — Consistência interna

    @Test
    @DisplayName("Custo SP→RJ deve ser igual ao custo RJ→SP (grafo simétrico)")
    void consistency_symmetric() {
        AStarResult spRj = aStar.findRoute("SP", "RJ");
        AStarResult rjSp = aStar.findRoute("RJ", "SP");

        assertTrue(spRj.found && rjSp.found);
        assertEquals(spRj.totalCostBrl, rjSp.totalCostBrl, 0.01);
    }

    @Test
    @DisplayName("Adicionar ferrovias nunca deve aumentar o custo ótimo")
    void consistency_railwayNeverIncreasesCost() {
        AStarResult semFerro = aStar.findRoute("SP", "DF");

        aStar.rebuildGraphWithRailways(Set.of(
                "SP-RJ", "RJ-SP", "RJ-MG", "MG-RJ",
                "MG-DF", "DF-MG", "SP-MG", "MG-SP"
        ));
        AStarResult comFerro = aStar.findRoute("SP", "DF");

        assertTrue(semFerro.found && comFerro.found);
        assertTrue(comFerro.totalCostBrl <= semFerro.totalCostBrl + 0.01);
    }

    @Test
    @DisplayName("edges.size() deve ser exatamente route.size() - 1")
    void consistency_edgesAndRouteSize() {
        AStarResult r = aStar.findRoute("RS", "PA");
        assertTrue(r.found);
        assertEquals(r.route.size() - 1, r.edges.size());
    }

    @Test
    @DisplayName("Nenhuma aresta retornada deve ter modal NONE")
    void consistency_noNoneModeInEdges() {
        aStar.rebuildGraphWithRailways(Set.of("SP-RJ", "RJ-SP", "RJ-MG", "MG-RJ"));
        AStarResult r = aStar.findRoute("SP", "MG");

        assertTrue(r.found);
        r.edges.forEach(e ->
                assertNotEquals(TransportMode.NONE, e.mode,
                        "Modal NONE é interno e nunca deve aparecer nas arestas retornadas")
        );
    }

    // Helper

    /** Conta quantas trocas de modal existem na rota retornada. */
    private int countTransfers(AStarResult r) {
        if (r.edges.size() <= 1) return 0;
        int count = 0;
        for (int i = 1; i < r.edges.size(); i++) {
            if (r.edges.get(i).mode != r.edges.get(i - 1).mode) count++;
        }
        return count;
    }
}