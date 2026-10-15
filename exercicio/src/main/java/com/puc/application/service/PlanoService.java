package com.puc.application.service;

import com.puc.adapters.out.persistence.PlanoApprovalPersistence;
import com.puc.application.port.in.PlanoUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PlanoService implements PlanoUseCase {
 private final PlanoApprovalPersistence persistence; private final Clock clock;
 public PlanoService(PlanoApprovalPersistence persistence){this(persistence,Clock.systemDefaultZone());}
 PlanoService(PlanoApprovalPersistence persistence,Clock clock){this.persistence=persistence;this.clock=clock;}
 @Transactional public void aprovar(UUID id){persistence.aprovar(id);}
 @Transactional public void cancelar(UUID id){persistence.cancelar(id,LocalDateTime.now(clock));}
}
