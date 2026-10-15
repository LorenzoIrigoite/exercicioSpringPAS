package com.puc.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

final class Slots {
    private Slots() { }

    static List<LocalDateTime> calcular(LocalDateTime inicio, LocalDateTime fim) {
        LocalDateTime slot = inicio.withMinute(0).withSecond(0).withNano(0);
        List<LocalDateTime> slots = new ArrayList<>();
        while (slot.isBefore(fim)) {
            slots.add(slot);
            slot = slot.plusHours(1);
        }
        return List.copyOf(slots);
    }
}
