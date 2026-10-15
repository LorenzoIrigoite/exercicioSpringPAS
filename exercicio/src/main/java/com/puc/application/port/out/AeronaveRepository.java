package com.puc.application.port.out;

import com.puc.domain.Aeronave;

import java.util.List;
import java.util.Optional;

public interface AeronaveRepository extends CadastroRepository<Aeronave> {
    Optional<Aeronave> porPrefixo(String prefixo);
    List<Aeronave> todos();
}
