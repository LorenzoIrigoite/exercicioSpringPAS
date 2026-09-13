package com.puc.controller;

import com.puc.dto.NovoLivroRequest;
import com.puc.model.Livro;
import com.puc.service.LivroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/livros")
public class LivroController {

    private final LivroService service;

    @Autowired
    public LivroController(LivroService service) {
        this.service = service;
    }

    @GetMapping
    public List<Livro> lista() {
        return service.listaLivros();
    }

    @GetMapping("/{id}")
    public Livro busca(@PathVariable Long id) {
        return service.buscaLivro(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Livro cadastra(@Valid @RequestBody NovoLivroRequest novoLivro) {
        return service.cadastraLivro(novoLivro);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long id) {
        service.removeLivro(id);
    }
}
