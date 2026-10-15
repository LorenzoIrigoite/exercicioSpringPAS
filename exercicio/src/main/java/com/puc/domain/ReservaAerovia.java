package com.puc.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ReservaAerovia(UUID id, String planoId, Aerovia aerovia, int altitude,
                             LocalDateTime inicio, LocalDateTime fim) implements ReservaPorSlots {
    public ReservaAerovia {
        if (id == null || planoId == null || planoId.isBlank() || aerovia == null || inicio == null || fim == null || !inicio.isBefore(fim)) {
            throw new IllegalArgumentException("dados inválidos da reserva de aerovia");
        }
    }

    @Override
    public List<LocalDateTime> slots() {
        return Slots.calcular(inicio, fim);
    }
}
