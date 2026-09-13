package com.puc.controller;

import com.puc.dto.NovoUsuarioSistemaRequest;
import com.puc.model.UsuarioSistema;
import com.puc.service.UsuarioSistemaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios-sistema")
public class UsuarioSistemaController {

    private final UsuarioSistemaService service;

    @Autowired
    public UsuarioSistemaController(UsuarioSistemaService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioSistema cadastra(@Valid @RequestBody NovoUsuarioSistemaRequest novoUsuario) {
        return service.cadastraUsuarioSistema(novoUsuario);
    }
}
