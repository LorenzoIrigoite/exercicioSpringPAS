package com.puc.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class AprovacaoPlanoDeVoo {
    public ResultadoAprovacao aprovar(PlanoDeVoo plano, List<? extends ReservaPorSlots> existentes) {
        if (plano == null || existentes == null) throw new IllegalArgumentException("plano e reservas são obrigatórios");
        if (plano.situacao() != SituacaoPlano.EM_EDICAO) return ResultadoAprovacao.falha(List.of("plano não está em edição"));
        ResultadoValidacao validacao = plano.validar();
        if (!validacao.valido()) return ResultadoAprovacao.falha(validacao.erros());

        List<ReservaPorSlots> geradas = gerar(plano);
        List<ReservaPorSlots> todas = new ArrayList<>(existentes);
        todas.addAll(geradas);
        List<String> conflitos = conflitos(todas);
        if (!conflitos.isEmpty()) return ResultadoAprovacao.falha(conflitos);
        plano.aprovar();
        return ResultadoAprovacao.sucesso(geradas);
    }

    public List<ReservaPorSlots> cancelar(PlanoDeVoo plano, LocalDateTime instante, List<? extends ReservaPorSlots> reservas) {
        if (plano == null || reservas == null) throw new IllegalArgumentException("plano e reservas são obrigatórios");
        plano.cancelar(instante);
        return reservas.stream().filter(reserva -> !plano.identificador().equals(planoId(reserva)))
                .map(reserva -> (ReservaPorSlots) reserva).toList();
    }

    private List<ReservaPorSlots> gerar(PlanoDeVoo plano) {
        List<ReservaPorSlots> reservas = new ArrayList<>();
        for (Trecho trecho : plano.trechos()) {
            reservas.add(new ReservaAerovia(UUID.randomUUID(), plano.identificador(), trecho.aerovia(), trecho.altitude(), trecho.entrada(), trecho.chegada()));
            if (trecho.operacaoChegada() == OperacaoChegada.POUSO) {
                LocalDateTime fimVaga = trecho.proximaEntrada();
                if (!trecho.chegada().isBefore(fimVaga)) fimVaga = trecho.chegada().plusHours(1);
                reservas.add(new ReservaVaga(UUID.randomUUID(), plano.identificador(), trecho.aerovia().destino(), trecho.vaga(), trecho.chegada(), fimVaga));
            }
        }
        return reservas;
    }

    private List<String> conflitos(List<ReservaPorSlots> reservas) {
        List<String> erros = new ArrayList<>();
        for (int i = 0; i < reservas.size(); i++) {
            for (int j = i + 1; j < reservas.size(); j++) {
                ReservaPorSlots a = reservas.get(i), b = reservas.get(j);
                if (!sobrepoe(a, b)) continue;
                if (a instanceof ReservaAerovia aa && b instanceof ReservaAerovia bb
                        && aa.aerovia().identificador().equals(bb.aerovia().identificador()) && aa.altitude() == bb.altitude()) erros.add("conflito de aerovia e altitude");
                if (a instanceof ReservaVaga aa && b instanceof ReservaVaga bb
                        && aa.aeroporto().codigo().equals(bb.aeroporto().codigo()) && aa.vaga() == bb.vaga()) erros.add("conflito de vaga e aeroporto");
            }
        }
        return erros;
    }

    private boolean sobrepoe(ReservaPorSlots a, ReservaPorSlots b) {
        Set<LocalDateTime> slots = new HashSet<>(a.slots());
        slots.retainAll(b.slots());
        return !slots.isEmpty();
    }

    private String planoId(ReservaPorSlots reserva) {
        return reserva instanceof ReservaAerovia a ? a.planoId() : ((ReservaVaga) reserva).planoId();
    }
}
