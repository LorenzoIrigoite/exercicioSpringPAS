package com.puc.application.port.out;

import com.puc.domain.Aerovia;

import java.util.List;
import java.util.Optional;

public interface AeroviaRepository extends CadastroRepository<Aerovia> {
    Optional<Aerovia> porIdentificador(String identificador);
    List<Aerovia> todos();
}
