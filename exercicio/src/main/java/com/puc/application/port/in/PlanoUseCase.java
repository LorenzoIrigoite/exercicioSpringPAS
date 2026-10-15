package com.puc.application.port.in;

import java.util.UUID;

public interface PlanoUseCase {
    void aprovar(UUID id);
    void cancelar(UUID id);
}
