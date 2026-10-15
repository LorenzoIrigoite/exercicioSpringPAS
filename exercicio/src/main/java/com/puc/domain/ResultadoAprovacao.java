package com.puc.domain;

import java.util.List;

public record ResultadoAprovacao(boolean aprovado, List<ReservaPorSlots> reservas, List<String> erros) {
    public ResultadoAprovacao {
        reservas = List.copyOf(reservas);
        erros = List.copyOf(erros);
    }

    static ResultadoAprovacao sucesso(List<ReservaPorSlots> reservas) {
        return new ResultadoAprovacao(true, reservas, List.of());
    }

    static ResultadoAprovacao falha(List<String> erros) {
        return new ResultadoAprovacao(false, List.of(), erros);
    }
}
