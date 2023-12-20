package br.com.diefenthaeler.springblogmongodb.service.impl;

import br.com.diefenthaeler.springblogmongodb.model.Autor;
import br.com.diefenthaeler.springblogmongodb.repository.AutorRepository;
import br.com.diefenthaeler.springblogmongodb.service.AutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AutorServiceImpl implements AutorService {

    private final AutorRepository autorRepository;

    @Override
    public Autor obterPorCodigo(String codigo) {
        return this.autorRepository
                .findById(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Autor nao encontrado!"));
    }

    @Override
    public Autor criar(Autor autor) {
        return autorRepository.save(autor);
    }
}
