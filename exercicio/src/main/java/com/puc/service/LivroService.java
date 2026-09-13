package com.puc.service;

import com.puc.dto.NovoLivroRequest;
import com.puc.exception.RecursoNaoEncontradoException;
import com.puc.model.Livro;
import com.puc.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LivroService {

    private final LivroRepository repository;

    @Autowired
    public LivroService(LivroRepository repository) {
        this.repository = repository;
    }

    public Livro cadastraLivro(NovoLivroRequest novoLivro) {
        var livro = new Livro(novoLivro.titulo().trim(), novoLivro.autor().trim(), novoLivro.ano());
        return repository.save(livro);
    }

    public List<Livro> listaLivros() {
        return repository.findAll();
    }

    public Livro buscaLivro(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado"));
    }

    public void removeLivro(Long id) {
        Livro livro = buscaLivro(id);
        repository.delete(livro);
    }
}
