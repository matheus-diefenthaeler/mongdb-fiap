package br.com.diefenthaeler.springblogmongodb.service;

import br.com.diefenthaeler.springblogmongodb.model.Artigo;
import br.com.diefenthaeler.springblogmongodb.model.ArtigoStatusCount;
import br.com.diefenthaeler.springblogmongodb.model.Autor;
import br.com.diefenthaeler.springblogmongodb.model.AutorTotalArtigo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ArtigoService {

    List<Artigo> obterTodos();

    Artigo obterPorCodigo(String codigo);

//    Artigo criar(Artigo artigo);

//    ResponseEntity<?> criar(Artigo artigo);

    ResponseEntity<?> criarArtigoComAutor(Artigo artigo, Autor autor);

    void excluirArtigoEAutor (Artigo artigo);
    ResponseEntity<?> atualizarArtigo(String id, Artigo artigo);

    List<Artigo> findByDataGreaterThan(LocalDateTime data);

    List<Artigo> findByDataAndStatus(LocalDateTime data, Integer status);

    void atualizar(Artigo artigo);

    void atualizarArtigo(String id, String novaUrl);

    void deleteById(String id);

    void deleteArtigoById(String id);

    List<Artigo> findByStatusAndDataGreaterThan(Integer status, LocalDateTime data);

    List<Artigo> obterArtigoPorDataHora(LocalDateTime de, LocalDateTime ate);

    List<Artigo> encontrarArtigosComplexosa(
            Integer status
            , LocalDateTime data
            , String titulo);

    Page<Artigo> listaArtigo(Pageable pageable);

    List<Artigo> findByStatusOrderByTituloAsc(Integer status);

    List<Artigo> obterArtigoPorStatusComOrdenacao(Integer status);

    List<Artigo> findByTexto(String searchTerm);

    List<ArtigoStatusCount> contarArtigosPorStatus();

    List<AutorTotalArtigo> calcularTotalArtigosPorAutorNoPeriodo(LocalDate dataInicio,
                                                                 LocalDate dataFim);
}
