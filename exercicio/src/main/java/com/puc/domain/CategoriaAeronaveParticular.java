package com.puc.domain;

public final class CategoriaAeronaveParticular implements CategoriaAeronave {
    public boolean altitudePermitida(int altitude) { return altitude >= 25000 && altitude <= 27000 && altitude % 1000 == 0; }
    public boolean janelaPermitida(Trecho trecho) { return true; }
}
