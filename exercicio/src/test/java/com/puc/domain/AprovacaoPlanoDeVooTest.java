package com.puc.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AprovacaoPlanoDeVooTest {
    private final Aeroporto poa = new Aeroporto("POA", "Porto Alegre", 2);
    private final Aeroporto fln = new Aeroporto("FLN", "Florianópolis", 2);
    private final Aerovia r1 = new Aerovia("R1", poa, fln, 60);
    private final LocalDateTime partida = LocalDateTime.of(2026, 10, 14, 22, 0);

    @Test
    void aprovaECalculaReservaDeAeroviaEVagaComPermanenciaAtravessandoDia() {
        PlanoDeVoo plano = plano("P1", 1, 90);

        ResultadoAprovacao resultado = new AprovacaoPlanoDeVoo().aprovar(plano, List.of());

        assertTrue(resultado.aprovado());
        assertEquals(SituacaoPlano.APROVADO, plano.situacao());
        assertEquals(2, resultado.reservas().size());
        ReservaAerovia aerovia = (ReservaAerovia) resultado.reservas().get(0);
        ReservaVaga vaga = (ReservaVaga) resultado.reservas().get(1);
        assertEquals(List.of(partida), aerovia.slots());
        assertEquals(List.of(partida.plusHours(1), partida.plusHours(2)), vaga.slots());
    }

    @Test
    void rejeitaConflitoDeAeroviaEnaoDeixaReservaParcial() {
        PlanoDeVoo plano = plano("P2", 1, 0);
        ReservaAerovia existente = new ReservaAerovia(java.util.UUID.randomUUID(), "OUTRO", r1, 25000,
                partida, partida.plusHours(1));

        ResultadoAprovacao resultado = new AprovacaoPlanoDeVoo().aprovar(plano, List.of(existente));

        assertFalse(resultado.aprovado());
        assertEquals(SituacaoPlano.EM_EDICAO, plano.situacao());
        assertTrue(resultado.reservas().isEmpty());
    }

    @Test
    void rejeitaConflitoDeVagaMesmaAeroportoEVaga() {
        PlanoDeVoo plano = plano("P3", 1, 30);
        ReservaVaga existente = new ReservaVaga(java.util.UUID.randomUUID(), "OUTRO", fln, 1,
                partida.plusHours(1), partida.plusHours(3));

        ResultadoAprovacao resultado = new AprovacaoPlanoDeVoo().aprovar(plano, List.of(existente));

        assertFalse(resultado.aprovado());
        assertEquals(SituacaoPlano.EM_EDICAO, plano.situacao());
    }

    @Test
    void conflitoEntreReservasGeradasTambemFalhaSemAprovar() {
        PlanoDeVoo primeiro = plano("P1", 1, 0);
        PlanoDeVoo segundo = plano("P2", 1, 0);
        AprovacaoPlanoDeVoo servico = new AprovacaoPlanoDeVoo();
        List<ReservaPorSlots> reservas = new ArrayList<>(servico.aprovar(primeiro, List.of()).reservas());

        ResultadoAprovacao resultado = servico.aprovar(segundo, reservas);

        assertFalse(resultado.aprovado());
        assertEquals(SituacaoPlano.EM_EDICAO, segundo.situacao());
        assertEquals(2, reservas.size());
    }

    @Test
    void cancelamentoLiberaReservasAssociadas() {
        PlanoDeVoo plano = plano("P4", 1, 30);
        AprovacaoPlanoDeVoo servico = new AprovacaoPlanoDeVoo();
        List<ReservaPorSlots> reservas = new ArrayList<>(servico.aprovar(plano, List.of()).reservas());

        List<ReservaPorSlots> restantes = servico.cancelar(plano, partida.minusMinutes(1), reservas);

        assertEquals(SituacaoPlano.CANCELADO, plano.situacao());
        assertTrue(restantes.isEmpty());
    }

    private PlanoDeVoo plano(String id, int vaga, int permanencia) {
        return new PlanoDeVoo(id, "PILOTO", new Aeronave("PT-" + id, 60, 1000, new CategoriaAeronaveParticular()),
                poa, fln, partida, List.of(new Trecho(r1, 25000, partida, OperacaoChegada.POUSO, permanencia)));
    }
}
