package br.com.diefenthaeler.springblogmongodb.service.impl;

import br.com.diefenthaeler.springblogmongodb.model.Artigo;
import br.com.diefenthaeler.springblogmongodb.model.ArtigoStatusCount;
import br.com.diefenthaeler.springblogmongodb.model.Autor;
import br.com.diefenthaeler.springblogmongodb.model.AutorTotalArtigo;
import br.com.diefenthaeler.springblogmongodb.repository.ArtigoRepository;
import br.com.diefenthaeler.springblogmongodb.repository.AutorRepository;
import br.com.diefenthaeler.springblogmongodb.service.ArtigoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtigoServiceImpl implements ArtigoService {

    private final MongoTemplate mongoTemplate;

    private final MongoTransactionManager transactionManager;

    private final ArtigoRepository artigoRepository;
    private final AutorRepository autorRepository;

    @Override
    public List<Artigo> obterTodos() {
        return this.artigoRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Artigo obterPorCodigo(String codigo) {
        return this.artigoRepository
                .findById(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Artigo nao existe!"));
    }

    // salvando artigo de forma transacionada a nivel de codigo
    // Foi feito a configuracao de varios servidores rodando local pro monhodb
    @Override
    public ResponseEntity<?> criarArtigoComAutor(Artigo artigo, Autor autor) {

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        transactionTemplate.execute(status -> {

            try {

                //Iniciar a transacao
                autorRepository.save(autor);
                artigo.setData(LocalDateTime.now());
                artigo.setAutor(autor);
                artigoRepository.save(artigo);

            } catch (Exception ex) {
                // tratar o erro, e lancar a transacao de  volta em caso de excecao
                status.setRollbackOnly();
                throw new RuntimeException("Erro ao criar artigo com autor " + ex.getMessage());
            }
            return null;
        });

        return null;
    }

    @Override
    public void excluirArtigoEAutor(Artigo artigo) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        transactionTemplate.execute(status -> {

            try {
                //Iniciar a transacao
                artigoRepository.delete(artigo);
                Autor autor = artigo.getAutor();
                autorRepository.delete(autor);

            } catch (Exception ex) {
                // tratar o erro, e lancar a transacao de  volta em caso de excecao
                status.setRollbackOnly();
                throw new RuntimeException("Erro ao deletar artigo com autor " + ex.getMessage());
            }
            return null;
        });

    }


/*    @Override
    public ResponseEntity<?> criar(Artigo artigo) {
        if (artigo.getAutor().getCodigo() != null) {
            //Recuperar o autor
            Autor autor = this.autorRepository
                    .findById(artigo.getAutor().getCodigo())
                    .orElseThrow(() -> new IllegalArgumentException("Autor Inexistente!"));
            //Define o autor no artigo!
            artigo.setAutor(autor);
        } else {
            // Caso contrari, gravar o artigo sem o autor
            artigo.setAutor(null);
        }

        try {
            // Salva e retorna o artigo cadastrado com
            // seu respectivo autor ou nao.
            this.artigoRepository.save(artigo);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .build();
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Artigo ja existe na colecao!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar artigo! " + e.getMessage());
        }
    }*/

    @Override
    public ResponseEntity<?> atualizarArtigo(String id, Artigo artigo) {
        try {
            Artigo existenteArtigo = this.artigoRepository.findById(id).orElse(null);

            if (existenteArtigo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Artigo nao encontrado na colecao!");
            }

            //atualizar alguns dados do artigo existente
            existenteArtigo.setTitulo(artigo.getTitulo());
            existenteArtigo.setTexto(artigo.getTexto());
            existenteArtigo.setData(artigo.getData());

            this.artigoRepository.save(existenteArtigo);

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar o artigo! " + e.getMessage());
        }
    }

/*    @Transactional // Com essa anotacao, o metodo usa os recursos da programacao reativa do mongodb
    @Override
    public Artigo criar(Artigo artigo) {
        // Se o autor existe
        if (artigo.getAutor().getCodigo() != null) {
            //Recuperar o autor
            Autor autor = this.autorRepository
                    .findById(artigo.getAutor().getCodigo())
                    .orElseThrow(() -> new IllegalArgumentException("Autor Inexistente!"));

            //Define o autor no artigo!
            artigo.setAutor(autor);

        } else {
            // Caso contrari, gravar o artigo sem o autor
            artigo.setAutor(null);
        }

        try {
            // Salvo o artigo com o autor ja cadastrado!
            return this.artigoRepository.save(artigo);
        } catch (OptimisticLockingFailureException ex) {
            // desenvolver a estrategia

            //1. Recuperar o documento mais recente (na colecao Artigo)
            Artigo atualizado =
                    artigoRepository.findById(artigo.getCodigo()).orElse(null);

            if (atualizado != null) {
                //2 . atualizar os campos desejados
                atualizado.setTitulo(artigo.getTitulo());
                atualizado.setTexto(artigo.getTexto());
                atualizado.setStatus(artigo.getStatus());


                //3. Incrementar a versao manualmente do documento
                atualizado.setVersion(atualizado.getVersion() + 1);

                //4. Tentar salvar novamente
                return this.artigoRepository.save(artigo);
            } else {
                //Documento nao encontrado, tratar o erro adequadamente
                throw new RuntimeException("Artigo nao encontrado: " + artigo.getCodigo());
            }
        }


    }*/

    @Override
    public List<Artigo> findByDataGreaterThan(LocalDateTime data) {
        Query query = new Query(Criteria.where("data").gt(data));
        return mongoTemplate.find(query, Artigo.class);
    }

    @Override
    public List<Artigo> findByDataAndStatus(LocalDateTime data, Integer status) {
        Query query = new Query(Criteria.where("data")
                .is(data)
                .and("status")
                .is(status));
        return mongoTemplate.find(query, Artigo.class);
    }

    @Transactional // Com essa anotacao, o metodo usa os recursos da programacao reativa do mongodb
    @Override
    public void atualizar(Artigo updateArtigo) {
        this.artigoRepository.save(updateArtigo);
    }


    @Transactional // Com essa anotacao, o metodo usa os recursos da programacao reativa do mongodb
    @Override
    public void atualizarArtigo(String id, String novaUrl) {
        // Criterio de busca pelo _id
        Query query = new Query(Criteria.where("_id")
                .is(id));

        //Definando os campos que serao atualizados
        Update update = new Update().set("url", novaUrl);

        // Executo a atualizacao
        this.mongoTemplate.updateFirst(query, update, Artigo.class);
    }

    @Transactional // Com essa anotacao, o metodo usa os recursos da programacao reativa do mongodb
    @Override
    public void deleteById(String id) {
        this.artigoRepository.deleteById(id);
    }

    @Override
    public void deleteArtigoById(String id) {
        Query query = new Query(Criteria.where("_id")
                .is(id)
        );

        mongoTemplate.remove(query, Artigo.class);
    }

    @Override
    public List<Artigo> findByStatusAndDataGreaterThan(Integer status, LocalDateTime data) {
        return this.artigoRepository.findByStatusAndDataGreaterThan(status, data);
    }

    @Override
    public List<Artigo> obterArtigoPorDataHora(LocalDateTime de, LocalDateTime ate) {
        return this.artigoRepository.obterArtigoPorDataHora(de, ate);
    }

    @Override
    public List<Artigo> encontrarArtigosComplexosa(Integer status, LocalDateTime data, String titulo) {
        Criteria criteria = new Criteria();

        // Filtrar artigos co mdata menor ou igual ao valor fornecido
        criteria.and("data").lte(data);

        // Filtrar artigos com o status especificado
        if (status != null) {
            criteria.and("status").is(status);
        }

        //Filtrar artigos cujo titulo existe
        if (titulo != null && !titulo.isEmpty()) {
            // ignora case sensitive
            criteria.and("titulo").regex(titulo, "i");
        }

        Query query = new Query(criteria);
        return mongoTemplate.find(query, Artigo.class);

    }

    //Paginado e ordenado
    @Override
    public Page<Artigo> listaArtigo(Pageable pageable) {
        Sort sort = Sort.by("titulo").ascending();
        Pageable paginacao = PageRequest.of(pageable.getPageNumber(),
                pageable.getPageSize(),
                sort);
        return this.artigoRepository.findAll(paginacao);
    }

    @Override
    public List<Artigo> findByStatusOrderByTituloAsc(Integer status) {
        return this.artigoRepository.findByStatusOrderByTituloAsc(status);
    }

    @Override
    public List<Artigo> obterArtigoPorStatusComOrdenacao(Integer status) {
        return this.artigoRepository.obterArtigoPorStatusComOrdenacao(status);
    }

    @Override
    public List<Artigo> findByTexto(String searchTerm) {
        TextCriteria criteria =
                TextCriteria.forDefaultLanguage().matchingPhrase(searchTerm);

        Query query = TextQuery.queryText(criteria).sortByScore();

        return mongoTemplate.find(query, Artigo.class);
    }

    //Contagem de artigos por status, funcao de agregracao

    @Override
    public List<ArtigoStatusCount> contarArtigosPorStatus() {
        TypedAggregation<Artigo> aggregation =
                Aggregation.newAggregation(
                        Artigo.class,
                        Aggregation.group("status").count().as(
                                "quantidade"
                        ),
                        Aggregation.project("quantidade")
                                .and("status").previousOperation()
                );

        AggregationResults<ArtigoStatusCount> result =
                mongoTemplate.aggregate(aggregation, ArtigoStatusCount.class);

        return result.getMappedResults();
    }

    // Numero de artigos publicado por autor em um determinado Periodo
    @Override
    public List<AutorTotalArtigo> calcularTotalArtigosPorAutorNoPeriodo(LocalDate dataInicio,
                                                                        LocalDate dataFim) {
        TypedAggregation<Artigo> aggregation =
                Aggregation.newAggregation(
                        Artigo.class,
                        Aggregation.match(
                                Criteria.where("data")
                                        .gte(dataInicio.atStartOfDay())
                                        .lt(dataFim.plusDays(1).atStartOfDay())),
                        Aggregation.group("autor")
                                .count().as("totalArtigos"),
                        Aggregation.project("totalArtigos").and("autor")
                                .previousOperation()
                );

        AggregationResults<AutorTotalArtigo> result =
                mongoTemplate.aggregate(aggregation, AutorTotalArtigo.class);

        return result.getMappedResults();
    }

}
