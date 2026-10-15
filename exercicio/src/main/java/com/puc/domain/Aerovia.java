package com.puc.domain;

public record Aerovia(String identificador, Aeroporto origem, Aeroporto destino, double distanciaQuilometros) {
    public Aerovia {
        if (identificador == null || identificador.isBlank() || origem == null || destino == null || !Double.isFinite(distanciaQuilometros) || distanciaQuilometros <= 0) throw new IllegalArgumentException("dados inválidos da aerovia");
    }
}
