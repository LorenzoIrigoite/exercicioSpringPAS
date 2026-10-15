package com.puc.domain;

public record Piloto(String codigo, String nome) {
    public Piloto {
        if (codigo == null || codigo.isBlank() || nome == null || nome.isBlank()) throw new IllegalArgumentException("dados inválidos do piloto");
    }
}
