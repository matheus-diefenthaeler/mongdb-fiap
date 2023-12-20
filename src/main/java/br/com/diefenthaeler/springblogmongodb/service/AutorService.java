package br.com.diefenthaeler.springblogmongodb.service;

import br.com.diefenthaeler.springblogmongodb.model.Autor;

import java.util.List;

public interface AutorService {

    Autor obterPorCodigo(String codigo);

    Autor criar(Autor artigo);
}
