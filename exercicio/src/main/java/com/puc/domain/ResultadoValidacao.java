package com.puc.domain;

import java.util.List;

public record ResultadoValidacao(boolean valido, List<String> erros) {
    public static ResultadoValidacao de(List<String> erros) { return new ResultadoValidacao(erros.isEmpty(), List.copyOf(erros)); }
}
