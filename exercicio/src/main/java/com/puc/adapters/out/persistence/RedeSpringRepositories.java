package com.puc.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

interface PilotoSpringRepository extends JpaRepository<PilotoJpaEntity, UUID> {}
interface AeronaveSpringRepository extends JpaRepository<AeronaveJpaEntity, UUID> {}
interface AeroviaSpringRepository extends JpaRepository<AeroviaJpaEntity, UUID> {}
interface PlanoSpringRepository extends JpaRepository<PlanoJpaEntity, UUID> {}
interface TrechoSpringRepository extends JpaRepository<TrechoJpaEntity, UUID> {}
interface ReservaAeroviaSpringRepository extends JpaRepository<ReservaAeroviaJpaEntity, UUID> {}
interface ReservaVagaSpringRepository extends JpaRepository<ReservaVagaJpaEntity, UUID> {}
