package com.puc.adapters.out.persistence;

import com.puc.application.port.out.*;
import com.puc.domain.*;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
class PilotoPersistenceAdapter implements PilotoRepository {
 private final PilotoSpringRepository repository;
 PilotoPersistenceAdapter(PilotoSpringRepository repository){this.repository=repository;}
 public Piloto save(Piloto p){repository.save(new PilotoJpaEntity(p.codigo(),p.nome()));return p;}
 public Optional<Piloto> porCodigo(String c){return repository.findAll().stream().filter(p->p.codigo.equals(c)).map(p->new Piloto(p.codigo,p.nome)).findFirst();}
 public List<Piloto> todos(){return repository.findAll().stream().map(p->new Piloto(p.codigo,p.nome)).toList();}
}

@Component
class AeronavePersistenceAdapter implements AeronaveRepository {
 private final AeronaveSpringRepository repository;
 AeronavePersistenceAdapter(AeronaveSpringRepository repository){this.repository=repository;}
 public Aeronave save(Aeronave a){AeronaveJpaEntity e=new AeronaveJpaEntity();e.prefixo=a.prefixo();e.velocidade=a.velocidadeCruzeiro();e.autonomia=a.autonomiaNominal();e.categoria=a.categoria() instanceof CategoriaAeronaveParticular?CategoriaPersistida.PARTICULAR:a.categoria() instanceof CategoriaAeronaveComercialPassageiros?CategoriaPersistida.COMERCIAL_PASSAGEIROS:CategoriaPersistida.COMERCIAL_CARGA;repository.save(e);return a;}
 public Optional<Aeronave> porPrefixo(String p){return Optional.empty();}
 public List<Aeronave> todos(){return List.of();}
}

@Component
class AeroviaPersistenceAdapter implements AeroviaRepository {
 private final AeroviaSpringRepository repository; private final AeroportoSpringRepository aeroportos;
 AeroviaPersistenceAdapter(AeroviaSpringRepository repository,AeroportoSpringRepository aeroportos){this.repository=repository;this.aeroportos=aeroportos;}
 public Aerovia save(Aerovia a){AeroviaJpaEntity e=new AeroviaJpaEntity();e.identificador=a.identificador();e.distancia=a.distanciaQuilometros();e.origem=aeroportos.findByCodigo(a.origem().codigo()).orElseThrow();e.destino=aeroportos.findByCodigo(a.destino().codigo()).orElseThrow();repository.save(e);return a;}
 public Optional<Aerovia> porIdentificador(String id){return Optional.empty();}
 public List<Aerovia> todos(){return repository.findAll().stream().map(e->new Aerovia(e.identificador,new Aeroporto(e.origem.getCodigo(),e.origem.getNome(),e.origem.getVagasEstacionamento()),new Aeroporto(e.destino.getCodigo(),e.destino.getNome(),e.destino.getVagasEstacionamento()),e.distancia)).toList();}
}
