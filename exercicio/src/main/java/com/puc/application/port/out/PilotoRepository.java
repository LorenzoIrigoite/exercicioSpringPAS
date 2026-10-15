package com.puc.application.port.out;

import com.puc.domain.Piloto;

import java.util.List;
import java.util.Optional;

public interface PilotoRepository extends CadastroRepository<Piloto> {
    Optional<Piloto> porCodigo(String codigo);
    List<Piloto> todos();
}
