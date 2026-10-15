package com.puc.application.port.out;

public interface CadastroRepository<T> {
    T save(T entity);
}
