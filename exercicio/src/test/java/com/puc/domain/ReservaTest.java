package com.puc.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReservaTest {
    private final Aeroporto poa = new Aeroporto("POA", "Porto Alegre", 10);
    private final Aeroporto fln = new Aeroporto("FLN", "Florianópolis", 10);
    private final Aerovia aerovia = new Aerovia("R1", poa, fln, 363);
    private final LocalDateTime inicio = LocalDateTime.of(2026, 10, 14, 15, 52);
    private final LocalDateTime fim = LocalDateTime.of(2026, 10, 14, 17, 7);

    @Test
    void reservaDeAeroviaCalculaSlotsAlinhadosSemArmazenarSlots() {
        ReservaAerovia reserva = new ReservaAerovia(UUID.randomUUID(), "PLANO-1", aerovia, 25000, inicio, fim);

        assertEquals(List.of(LocalDateTime.of(2026, 10, 14, 15, 0),
                LocalDateTime.of(2026, 10, 14, 16, 0),
                LocalDateTime.of(2026, 10, 14, 17, 0)), reserva.slots());
    }

    @Test
    void reservaDeVagaCalculaSlotsIncluindoPermanenciaAteFim() {
        ReservaVaga reserva = new ReservaVaga(UUID.randomUUID(), "PLANO-1", poa, 3, inicio, fim);

        assertEquals(3, reserva.slots().size());
        assertEquals(LocalDateTime.of(2026, 10, 14, 17, 0), reserva.slots().get(2));
    }

    @Test
    void rejeitaIntervaloInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> new ReservaAerovia(UUID.randomUUID(), "PLANO-1", aerovia, 25000, fim, inicio));
    }
}
