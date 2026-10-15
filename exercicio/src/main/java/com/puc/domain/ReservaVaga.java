package com.puc.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ReservaVaga(UUID id, String planoId, Aeroporto aeroporto, int vaga,
                          LocalDateTime inicio, LocalDateTime fim) implements ReservaPorSlots {
    public ReservaVaga {
        if (id == null || planoId == null || planoId.isBlank() || aeroporto == null || vaga <= 0 || vaga > aeroporto.vagasEstacionamento() || inicio == null || fim == null || !inicio.isBefore(fim)) {
            throw new IllegalArgumentException("dados inválidos da reserva de vaga");
        }
    }

    public String planoId() { return planoId; }

    @Override
    public List<LocalDateTime> slots() {
        return Slots.calcular(inicio, fim);
    }
}
