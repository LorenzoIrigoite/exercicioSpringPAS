package com.puc.infrastructure;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DadosIniciaisTest {
    @Test
    void migrationInicialDeclaraRedeCompletaEPlanosComReservas() throws IOException {
        String sql = new String(getClass().getResourceAsStream("/db/migration/V2__dados_iniciais.sql").readAllBytes(), StandardCharsets.UTF_8);

        assertTrue(sql.contains("'POA'"));
        assertTrue(sql.contains("'FLN'"));
        assertTrue(sql.contains("'CWB'"));
        assertTrue(sql.contains("'GRU'"));
        assertTrue(sql.lines().filter(line -> line.contains("'R")).count() >= 8);
        assertTrue(sql.contains("INSERT INTO plano_voo"));
        assertTrue(sql.contains("INSERT INTO reserva_aerovia"));
        assertTrue(sql.contains("INSERT INTO reserva_vaga"));
        assertTrue(sql.contains("2026-10-14"));
    }
}
