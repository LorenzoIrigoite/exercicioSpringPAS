package com.puc.domain;

public record CategoriaAeronaveComercialPassageiros(String companhia, int capacidadeMaxima) implements CategoriaAeronave {
    public CategoriaAeronaveComercialPassageiros {
        if (companhia == null || companhia.isBlank() || capacidadeMaxima <= 0) throw new IllegalArgumentException("dados inválidos da categoria de passageiros");
    }
    public boolean altitudePermitida(int altitude) { return altitude >= 28000 && altitude <= 35000 && altitude % 1000 == 0; }
    public boolean janelaPermitida(Trecho trecho) { return true; }
}
