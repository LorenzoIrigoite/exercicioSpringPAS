package com.puc.adapters.out.persistence;

import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name="aeroporto")
public class AeroportoJpaEntity {
 @Id @GeneratedValue private UUID id;
 @Column(nullable=false, unique=true) private String codigo;
 @Column(nullable=false) private String nome;
 @Column(name="vagas_estacionamento", nullable=false) private int vagasEstacionamento;
 protected AeroportoJpaEntity() {}
 public AeroportoJpaEntity(String codigo,String nome,int vagas){this.codigo=codigo;this.nome=nome;this.vagasEstacionamento=vagas;}
 public String getCodigo(){return codigo;} public String getNome(){return nome;} public int getVagasEstacionamento(){return vagasEstacionamento;}
}
