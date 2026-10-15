package com.puc.application.port.out;

import com.puc.domain.Aeroporto;

import java.util.List;
import java.util.Optional;

public interface AeroportoRepository extends CadastroRepository<Aeroporto> {
    Optional<Aeroporto> porCodigo(String codigo);
    List<Aeroporto> todos();
}
