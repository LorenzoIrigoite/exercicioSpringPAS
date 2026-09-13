package com.puc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NovoUsuarioSistemaRequest(
        @NotBlank @Size(max = 100) String username,
        @NotBlank @Size(min = 8, max = 100) String senha,
        @Pattern(regexp = "(?i)ADMIN|CONSULTA", message = "deve ser ADMIN ou CONSULTA")
        String perfil) {

    public String perfilNormalizado() {
        if (perfil == null || perfil.isBlank()) return "CONSULTA";
        return "ADMIN".equals(perfil.trim().toUpperCase()) ? "ADMIN" : "CONSULTA";
    }
}
