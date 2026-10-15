package com.puc.domain;

public record Aeroporto(String codigo, String nome, int vagasEstacionamento) {
    public Aeroporto {
        if (codigo == null || codigo.isBlank() || nome == null || nome.isBlank() || vagasEstacionamento <= 0) throw new IllegalArgumentException("dados inválidos do aeroporto");
    }
}
