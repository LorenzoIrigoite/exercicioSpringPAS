package com.puc.adapters.out.persistence;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

interface PlanoApprovalRepository extends org.springframework.data.jpa.repository.JpaRepository<PlanoJpaEntity, UUID> {
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select p from PlanoJpaEntity p where p.id = :id") java.util.Optional<PlanoJpaEntity> findLocked(@Param("id") UUID id);
}
@Component
public class PlanoApprovalPersistence {
 private final PlanoApprovalRepository repository;
 public PlanoApprovalPersistence(PlanoApprovalRepository repository){this.repository=repository;}
 @Transactional public void aprovar(UUID id){PlanoJpaEntity p=repository.findLocked(id).orElseThrow();if(p.situacao!=SituacaoPersistida.EM_EDICAO)throw new IllegalStateException("plano não está em edição");p.situacao=SituacaoPersistida.APROVADO;}
 @Transactional public void cancelar(UUID id, LocalDateTime agora){PlanoJpaEntity p=repository.findLocked(id).orElseThrow();if(p.situacao!=SituacaoPersistida.APROVADO||!p.partida.isAfter(agora))throw new IllegalStateException("plano não pode ser cancelado");p.situacao=SituacaoPersistida.CANCELADO;}
}
