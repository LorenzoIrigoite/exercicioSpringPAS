package com.puc.domain;

public record Aeronave(String prefixo, double velocidadeCruzeiro, double autonomiaNominal, CategoriaAeronave categoria) {
    public Aeronave {
        if (prefixo == null || prefixo.isBlank() || !Double.isFinite(velocidadeCruzeiro) || velocidadeCruzeiro <= 0 || !Double.isFinite(autonomiaNominal) || autonomiaNominal <= 0 || categoria == null) throw new IllegalArgumentException("dados inválidos da aeronave");
    }
}
