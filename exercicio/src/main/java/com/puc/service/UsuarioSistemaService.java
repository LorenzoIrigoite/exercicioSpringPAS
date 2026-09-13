package com.puc.service;

import com.puc.dto.NovoUsuarioSistemaRequest;
import com.puc.exception.RecursoDuplicadoException;
import com.puc.model.UsuarioSistema;
import com.puc.repository.UsuarioSistemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioSistemaService {

    private final UsuarioSistemaRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioSistemaService(UsuarioSistemaRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioSistema cadastraUsuarioSistema(NovoUsuarioSistemaRequest novoUsuario) {
        String username = novoUsuario.username().trim();
        String senha = novoUsuario.senha().trim();

        if (repository.existsByUsername(username)) {
            throw new RecursoDuplicadoException("Usuário já cadastrado");
        }

        var usuario = new UsuarioSistema(username, passwordEncoder.encode(senha),
                novoUsuario.perfilNormalizado());
        return repository.save(usuario);
    }
}
