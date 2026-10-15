package com.puc.domain;

import java.time.LocalTime;

public record CategoriaAeronaveComercialCarga(String companhia, double capacidadeMaximaToneladas) implements CategoriaAeronave {
    public CategoriaAeronaveComercialCarga {
        if (companhia == null || companhia.isBlank() || !Double.isFinite(capacidadeMaximaToneladas) || capacidadeMaximaToneladas <= 0) throw new IllegalArgumentException("dados inválidos da categoria de carga");
    }
    public boolean altitudePermitida(int altitude) { return altitude >= 25000 && altitude <= 35000 && altitude % 1000 == 0; }
    public boolean janelaPermitida(Trecho trecho) { return trecho.entrada().toLocalDate().equals(trecho.chegada().toLocalDate()) && !trecho.entrada().toLocalTime().isBefore(LocalTime.MIDNIGHT) && trecho.chegada().toLocalTime().isBefore(LocalTime.of(6, 0)); }
}
