package com.puc.application.service;

import com.puc.application.port.in.CadastroRedeUseCase;
import com.puc.application.port.out.AeroportoRepository;
import com.puc.application.port.out.AeroviaRepository;
import com.puc.application.port.out.AeronaveRepository;
import com.puc.application.port.out.PilotoRepository;
import com.puc.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CadastroRedeService implements CadastroRedeUseCase {
    private final AeroportoRepository aeroportos;
    private final AeroviaRepository aerovias;
    private final PilotoRepository pilotos;
    private final AeronaveRepository aeronaves;

    public CadastroRedeService(AeroportoRepository aeroportos, AeroviaRepository aerovias, PilotoRepository pilotos, AeronaveRepository aeronaves) {
        this.aeroportos = aeroportos; this.aerovias = aerovias; this.pilotos = pilotos; this.aeronaves = aeronaves;
    }
    public Aeroporto cadastrar(Aeroporto aeroporto) { return aeroportos.save(aeroporto); }
    public Aerovia cadastrar(Aerovia aerovia) { return aerovias.save(aerovia); }
    public Piloto cadastrar(Piloto piloto) { return pilotos.save(piloto); }
    public Aeronave cadastrar(Aeronave aeronave) { return aeronaves.save(aeronave); }
    @Transactional(readOnly = true) public List<Aeroporto> aeroportos() { return aeroportos.todos(); }
    @Transactional(readOnly = true) public List<Aerovia> aerovias() { return aerovias.todos(); }
    @Transactional(readOnly = true) public List<Piloto> pilotos() { return pilotos.todos(); }
    @Transactional(readOnly = true) public List<Aeronave> aeronaves() { return aeronaves.todos(); }
}
