package com.puc.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class PlanoDeVoo {
    private final String identificador;
    private final String piloto;
    private final Aeronave aeronave;
    private final Aeroporto origem;
    private final Aeroporto destino;
    private final LocalDateTime partida;
    private final List<Trecho> trechos;
    private SituacaoPlano situacao = SituacaoPlano.EM_EDICAO;

    public PlanoDeVoo(String identificador, String piloto, Aeronave aeronave, Aeroporto origem, Aeroporto destino, LocalDateTime partida, List<Trecho> trechos) {
        if (identificador == null || identificador.isBlank() || piloto == null || piloto.isBlank() || aeronave == null || origem == null || destino == null || partida == null || trechos == null || trechos.stream().anyMatch(java.util.Objects::isNull)) throw new IllegalArgumentException("dados inválidos do plano");
        this.identificador = identificador; this.piloto = piloto; this.aeronave = aeronave; this.origem = origem; this.destino = destino; this.partida = partida; this.trechos = new ArrayList<>(trechos);
    }

    public String identificador() { return identificador; }
    public SituacaoPlano situacao() { return situacao; }
    public List<Trecho> trechos() { return List.copyOf(trechos); }

    public void editar(List<Trecho> novosTrechos) {
        exigir(SituacaoPlano.EM_EDICAO);
        if (novosTrechos == null || novosTrechos.stream().anyMatch(java.util.Objects::isNull)) throw new IllegalArgumentException("trechos inválidos");
        trechos.clear();
        trechos.addAll(novosTrechos);
    }

    public void abandonar() {
        exigir(SituacaoPlano.EM_EDICAO);
        situacao = SituacaoPlano.ABANDONADO;
    }

    public void aprovar() {
        exigir(SituacaoPlano.EM_EDICAO);
        if (!validar().valido()) throw new IllegalStateException("plano inválido");
        situacao = SituacaoPlano.APROVADO;
    }

    public void cancelar(LocalDateTime instante) {
        exigir(SituacaoPlano.APROVADO);
        if (instante == null || !partida.isAfter(instante)) throw new IllegalStateException("partida já ocorreu");
        situacao = SituacaoPlano.CANCELADO;
    }

    private void exigir(SituacaoPlano esperada) {
        if (situacao != esperada) throw new IllegalStateException("operação incompatível com situação do plano");
    }

    public ResultadoValidacao validar() {
        exigir(SituacaoPlano.EM_EDICAO);
        List<String> erros = new ArrayList<>();
        if (trechos.isEmpty()) { erros.add("plano precisa ter trechos"); return ResultadoValidacao.de(erros); }
        double distanciaSemAbastecimento = 0;
        LocalDateTime entradaEsperada = partida;
        for (int i = 0; i < trechos.size(); i++) {
            Trecho trecho = trechos.get(i);
            trecho.calcularChegada(aeronave);
            if (i == 0) {
                if (!trecho.entrada().equals(partida)) erros.add("entrada do primeiro trecho incompatível com partida");
                if (!trecho.aerovia().origem().codigo().equals(origem.codigo())) erros.add("origem do primeiro trecho incompatível");
            }
            if (i > 0) {
                Trecho anterior = trechos.get(i - 1);
                if (!anterior.aerovia().destino().codigo().equals(trecho.aerovia().origem().codigo())) erros.add("destino e origem do trecho não coincidem");
                if (!trecho.entrada().equals(entradaEsperada)) erros.add("entrada do trecho incompatível com chegada calculada");
            }
            if (!aeronave.categoria().altitudePermitida(trecho.altitude())) erros.add("altitude incompatível com categoria");
            if (!aeronave.categoria().janelaPermitida(trecho)) erros.add("trecho deve ocorrer entre 00:00 e 06:00");
            distanciaSemAbastecimento += trecho.aerovia().distanciaQuilometros();
            if (distanciaSemAbastecimento > aeronave.autonomiaNominal() * 0.85) erros.add("autonomia insuficiente");
            if (trecho.operacaoChegada() == OperacaoChegada.POUSO) distanciaSemAbastecimento = 0;
            entradaEsperada = trecho.proximaEntrada();
        }
        Trecho ultimo = trechos.get(trechos.size() - 1);
        if (!ultimo.aerovia().destino().codigo().equals(destino.codigo())) erros.add("destino do último trecho incompatível");
        if (ultimo.operacaoChegada() != OperacaoChegada.POUSO) erros.add("destino final exige pouso");
        if (!ultimo.chegada().toLocalDate().equals(partida.toLocalDate())) erros.add("partida e chegada devem ocorrer na mesma data");
        return ResultadoValidacao.de(erros);
    }
}
