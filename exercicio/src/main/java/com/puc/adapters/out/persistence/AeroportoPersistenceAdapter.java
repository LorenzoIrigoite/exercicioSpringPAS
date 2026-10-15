package com.puc.adapters.out.persistence;

import com.puc.application.port.out.AeroportoRepository;
import com.puc.domain.Aeroporto;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class AeroportoPersistenceAdapter implements AeroportoRepository {
 private final AeroportoSpringRepository repository;
 public AeroportoPersistenceAdapter(AeroportoSpringRepository repository){this.repository=repository;}
 public Aeroporto save(Aeroporto a){repository.save(new AeroportoJpaEntity(a.codigo(),a.nome(),a.vagasEstacionamento()));return a;}
 public Optional<Aeroporto> porCodigo(String codigo){return repository.findByCodigo(codigo).map(e->new Aeroporto(e.getCodigo(),e.getNome(),e.getVagasEstacionamento()));}
 public List<Aeroporto> todos(){return repository.findAll().stream().map(e->new Aeroporto(e.getCodigo(),e.getNome(),e.getVagasEstacionamento())).toList();}
}
