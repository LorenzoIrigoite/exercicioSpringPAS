package com.puc.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlanoDeVooTest {
    private final Aeroporto poa = new Aeroporto("POA", "Porto Alegre", 10);
    private final Aeroporto fln = new Aeroporto("FLN", "Florianópolis", 10);
    private final Aeroporto cwb = new Aeroporto("CWB", "Curitiba", 10);
    private final Aerovia poaFlorianopolis = new Aerovia("R1", poa, fln, 363);
    private final Aerovia florianopolisCuritiba = new Aerovia("R4", fln, cwb, 245);
    private final LocalDateTime partida = LocalDateTime.of(2026, 10, 14, 10, 0);

    @Test
    void aceitaPercursoContinuoECalculaChegada() {
        Aeronave aeronave = aeronave(new CategoriaAeronaveParticular(), 1000, 1000);
        Trecho trecho = new Trecho(poaFlorianopolis, 25000, partida, OperacaoChegada.POUSO, 30);
        PlanoDeVoo plano = plano(poa, fln, aeronave, List.of(trecho));

        ResultadoValidacao resultado = plano.validar();

        assertTrue(resultado.valido(), resultado.erros().toString());
        assertEquals(LocalDateTime.of(2026, 10, 14, 10, 22), trecho.chegada());
    }

    @Test
    void aceitaPercursoContinuo() {
        Aeronave aeronave = aeronave(new CategoriaAeronaveParticular(), 1000, 1000);
        Trecho primeiro = new Trecho(poaFlorianopolis, 25000, partida, OperacaoChegada.SOBREVOO, 0);
        Trecho segundo = new Trecho(florianopolisCuritiba, 25000, partida.plusMinutes(22), OperacaoChegada.POUSO, 0);
        PlanoDeVoo plano = plano(poa, cwb, aeronave, List.of(primeiro, segundo));

        ResultadoValidacao resultado = plano.validar();

        assertTrue(resultado.valido(), resultado.erros().toString());
    }

    @Test
    void rejeitaEntradaDoPrimeiroTrechoDiferenteDaPartida() {
        Aeronave aeronave = aeronave(new CategoriaAeronaveParticular(), 1000, 1000);
        Trecho trecho = new Trecho(poaFlorianopolis, 25000, partida.plusMinutes(1), OperacaoChegada.POUSO, 0);

        ResultadoValidacao resultado = plano(poa, fln, aeronave, List.of(trecho)).validar();

        assertFalse(resultado.valido());
        assertTrue(resultado.erros().stream().anyMatch(e -> e.contains("entrada do primeiro trecho")));
    }

    @Test
    void rejeitaSobrevooComPermanencia() {
        assertThrows(IllegalArgumentException.class,
                () -> new Trecho(poaFlorianopolis, 25000, partida, OperacaoChegada.SOBREVOO, 1));
    }

    @Test
    void rejeitaAltitudeIncompativelComCategoria() {
        Aeronave aeronave = aeronave(new CategoriaAeronaveParticular(), 1000, 1000);
        Trecho trecho = new Trecho(poaFlorianopolis, 28000, partida, OperacaoChegada.POUSO, 0);

        ResultadoValidacao resultado = plano(poa, fln, aeronave, List.of(trecho)).validar();

        assertFalse(resultado.valido());
        assertTrue(resultado.erros().stream().anyMatch(e -> e.contains("altitude")));
    }

    @Test
    void rejeitaCargaForaDaJanelaNoturna() {
        Aeronave aeronave = aeronave(new CategoriaAeronaveComercialCarga("Carga", 10), 1000, 1000);
        Trecho trecho = new Trecho(poaFlorianopolis, 30000, partida, OperacaoChegada.POUSO, 0);

        ResultadoValidacao resultado = plano(poa, fln, aeronave, List.of(trecho)).validar();

        assertFalse(resultado.valido());
        assertTrue(resultado.erros().stream().anyMatch(e -> e.contains("00:00")));
    }

    @Test
    void rejeitaCargaQueAtravessaData() {
        Aeronave aeronave = aeronave(new CategoriaAeronaveComercialCarga("Carga", 10), 1000, 1000);
        Aerovia curta = new Aerovia("NOITE", poa, fln, 50);
        Trecho trecho = new Trecho(curta, 30000, partida.withHour(23).withMinute(59), OperacaoChegada.POUSO, 0);

        ResultadoValidacao resultado = plano(poa, fln, aeronave, List.of(trecho)).validar();

        assertFalse(resultado.valido());
        assertTrue(resultado.erros().stream().anyMatch(e -> e.contains("00:00")));
    }

    @Test
    void aceitaAeroportosComMesmoValor() {
        Aeroporto origemEquivalente = new Aeroporto("POA", "Porto Alegre", 10);
        Aeronave aeronave = aeronave(new CategoriaAeronaveParticular(), 1000, 1000);
        Aerovia aerovia = new Aerovia("VALOR", origemEquivalente, fln, 363);
        Trecho trecho = new Trecho(aerovia, 25000, partida, OperacaoChegada.POUSO, 0);

        ResultadoValidacao resultado = plano(poa, fln, aeronave, List.of(trecho)).validar();

        assertTrue(resultado.valido(), resultado.erros().toString());
    }

    @Test
    void rejeitaArgumentosBasicosInvalidos() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new Aeroporto("", "Nome", 1)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Aeroporto("POA", "Nome", 0)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Aerovia("R", poa, fln, 0)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Aeronave("PT", 0, 1, new CategoriaAeronaveParticular())),
                () -> assertThrows(IllegalArgumentException.class, () -> new Aeronave("PT", Double.NaN, 1, new CategoriaAeronaveParticular())),
                () -> assertThrows(IllegalArgumentException.class, () -> new CategoriaAeronaveComercialPassageiros("", 1)),
                () -> assertThrows(IllegalArgumentException.class, () -> new CategoriaAeronaveComercialPassageiros("Companhia", 0))
        );
    }

    @Test
    void rejeitaTrechoNulo() {
        assertThrows(IllegalArgumentException.class, () -> plano(poa, fln, aeronave(new CategoriaAeronaveParticular(), 1000, 1000), java.util.Arrays.asList((Trecho) null)));
    }

    @Test
    void rejeitaTrechoComSegundosOuNanos() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new Trecho(poaFlorianopolis, 25000, partida.withSecond(1), OperacaoChegada.POUSO, 0)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Trecho(poaFlorianopolis, 25000, partida.withNano(1), OperacaoChegada.POUSO, 0))
        );
    }

    @Test
    void validarExigePlanoEmEdicao() {
        PlanoDeVoo plano = plano(poa, fln, aeronave(new CategoriaAeronaveParticular(), 1000, 1000), List.of());
        plano.abandonar();

        assertThrows(IllegalStateException.class, plano::validar);
    }

    @Test
    void nasceEmEdicaoEpermiteEditarTrechos() {
        PlanoDeVoo plano = plano(poa, fln, aeronave(new CategoriaAeronaveParticular(), 1000, 1000), List.of());
        Trecho trecho = new Trecho(poaFlorianopolis, 25000, partida, OperacaoChegada.POUSO, 0);

        assertEquals(SituacaoPlano.EM_EDICAO, plano.situacao());
        plano.editar(List.of(trecho));

        assertEquals(List.of(trecho), plano.trechos());
    }

    @Test
    void planoEmEdicaoPodeSerAbandonadoEficaImutavel() {
        PlanoDeVoo plano = plano(poa, fln, aeronave(new CategoriaAeronaveParticular(), 1000, 1000), List.of());

        plano.abandonar();

        assertEquals(SituacaoPlano.ABANDONADO, plano.situacao());
        assertThrows(IllegalStateException.class, () -> plano.editar(List.of()));
    }

    @Test
    void aprovaPlanoValidoEbloqueiaEdicao() {
        PlanoDeVoo plano = plano(poa, fln, aeronave(new CategoriaAeronaveParticular(), 1000, 1000),
                List.of(new Trecho(poaFlorianopolis, 25000, partida, OperacaoChegada.POUSO, 0)));

        plano.aprovar();

        assertEquals(SituacaoPlano.APROVADO, plano.situacao());
        assertThrows(IllegalStateException.class, () -> plano.editar(List.of()));
    }

    @Test
    void naoAprovaPlanoInvalido() {
        PlanoDeVoo plano = plano(poa, fln, aeronave(new CategoriaAeronaveParticular(), 1000, 1000), List.of());

        assertThrows(IllegalStateException.class, plano::aprovar);
        assertEquals(SituacaoPlano.EM_EDICAO, plano.situacao());
    }

    @Test
    void cancelaSomenteAprovadoComPartidaFutura() {
        PlanoDeVoo plano = plano(poa, fln, aeronave(new CategoriaAeronaveParticular(), 1000, 1000),
                List.of(new Trecho(poaFlorianopolis, 25000, partida, OperacaoChegada.POUSO, 0)));
        plano.aprovar();

        plano.cancelar(partida.minusMinutes(1));

        assertEquals(SituacaoPlano.CANCELADO, plano.situacao());
    }

    @Test
    void naoCancelaAntesDeAprovacaoNemDepoisDaPartida() {
        PlanoDeVoo emEdicao = plano(poa, fln, aeronave(new CategoriaAeronaveParticular(), 1000, 1000), List.of());
        assertThrows(IllegalStateException.class, () -> emEdicao.cancelar(partida.minusMinutes(1)));

        PlanoDeVoo aprovado = plano(poa, fln, aeronave(new CategoriaAeronaveParticular(), 1000, 1000),
                List.of(new Trecho(poaFlorianopolis, 25000, partida, OperacaoChegada.POUSO, 0)));
        aprovado.aprovar();
        assertThrows(IllegalStateException.class, () -> aprovado.cancelar(partida));
    }

    @Test
    void rejeitaAutonomiaInsuficiente() {
        Aeronave aeronave = aeronave(new CategoriaAeronaveParticular(), 1000, 400);
        Trecho trecho = new Trecho(poaFlorianopolis, 25000, partida, OperacaoChegada.POUSO, 0);

        ResultadoValidacao resultado = plano(poa, fln, aeronave, List.of(trecho)).validar();

        assertFalse(resultado.valido());
        assertTrue(resultado.erros().stream().anyMatch(e -> e.contains("autonomia")));
    }

    @Test
    void rejeitaAutonomiaEReiniciaAposPouso() {
        Aeronave aeronave = aeronave(new CategoriaAeronaveParticular(), 1000, 500);
        Trecho primeiro = new Trecho(poaFlorianopolis, 25000, partida, OperacaoChegada.POUSO, 30);
        Trecho segundo = new Trecho(florianopolisCuritiba, 25000, partida.plusMinutes(52), OperacaoChegada.POUSO, 0);

        ResultadoValidacao resultado = plano(poa, cwb, aeronave, List.of(primeiro, segundo)).validar();

        assertTrue(resultado.valido(), resultado.erros().toString());
    }

    @Test
    void rejeitaChegadaEmDataDiferenteDaPartida() {
        Aeronave aeronave = aeronave(new CategoriaAeronaveParticular(), 10, 10000);
        Aerovia longa = new Aerovia("LONGA", poa, fln, 200);
        Trecho trecho = new Trecho(longa, 25000, partida, OperacaoChegada.POUSO, 0);

        ResultadoValidacao resultado = plano(poa, fln, aeronave, List.of(trecho)).validar();

        assertFalse(resultado.valido());
        assertTrue(resultado.erros().stream().anyMatch(e -> e.contains("mesma data")));
    }

    private PlanoDeVoo plano(Aeroporto origem, Aeroporto destino, Aeronave aeronave, List<Trecho> trechos) {
        return new PlanoDeVoo("PLANO-1", "PILOTO-1", aeronave, origem, destino, partida, trechos);
    }

    private Aeronave aeronave(CategoriaAeronave categoria, double velocidade, double autonomia) {
        return new Aeronave("PT-ABC", velocidade, autonomia, categoria);
    }
}
