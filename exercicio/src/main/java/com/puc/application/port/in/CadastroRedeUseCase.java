package com.puc.application.port.in;

import com.puc.domain.Aeroporto;
import com.puc.domain.Aerovia;
import com.puc.domain.Aeronave;
import com.puc.domain.Piloto;

import java.util.List;

public interface CadastroRedeUseCase {
    Aeroporto cadastrar(Aeroporto aeroporto);
    Aerovia cadastrar(Aerovia aerovia);
    Piloto cadastrar(Piloto piloto);
    Aeronave cadastrar(Aeronave aeronave);
    List<Aeroporto> aeroportos();
    List<Aerovia> aerovias();
    List<Piloto> pilotos();
    List<Aeronave> aeronaves();
}
