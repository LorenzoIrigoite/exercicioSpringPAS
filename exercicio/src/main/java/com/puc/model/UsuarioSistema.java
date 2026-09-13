package com.puc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios_sistema")
public class UsuarioSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private String perfil;

    protected UsuarioSistema() {}

    public UsuarioSistema(String username, String senha, String perfil) {
        this.username = username;
        this.senha = senha;
        this.perfil = perfil;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getSenha() { return senha; }
    public String getPerfil() { return perfil; }
}
