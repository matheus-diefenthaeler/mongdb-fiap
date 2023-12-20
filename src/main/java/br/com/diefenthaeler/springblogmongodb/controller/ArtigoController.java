package br.com.diefenthaeler.springblogmongodb.controller;

import br.com.diefenthaeler.springblogmongodb.model.Artigo;
import br.com.diefenthaeler.springblogmongodb.model.ArtigoStatusCount;
import br.com.diefenthaeler.springblogmongodb.model.AutorTotalArtigo;
import br.com.diefenthaeler.springblogmongodb.service.ArtigoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/v1/artigos")
@RequiredArgsConstructor
public class ArtigoController {


    private final ArtigoService artigoService;

    @GetMapping
    public List<Artigo> obterTodos() {
        return this.artigoService.obterTodos();
    }

    @GetMapping("/{codigo}")
    public Artigo obterPorCodigo(@PathVariable String codigo) {
        return this.artigoService.obterPorCodigo(codigo);
    }

    @PostMapping
    public Artigo criar(@RequestBody Artigo artigo) {
        return this.artigoService.criar(artigo);
    }

    @GetMapping("/maiordata")
    public List<Artigo> findByDataGreaterThan(@RequestParam("data") LocalDateTime data) {
        return this.artigoService.findByDataGreaterThan(data);
    }

    @GetMapping("/data-status")
    public List<Artigo> findByDataAndStatus(@RequestParam("data") LocalDateTime data, @RequestParam("status") Integer status) {
        return this.artigoService.findByDataAndStatus(data, status);
    }

    @PutMapping
    public void atualizar(@RequestBody Artigo artigo) {
        this.artigoService.atualizar(artigo);
    }

    @PutMapping("/{id}")
    public void atualizarArtigo(@PathVariable String id,
                                @RequestBody String novaUrl) {
        this.artigoService.atualizarArtigo(id, novaUrl);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id) {
        this.artigoService.deleteById(id);
    }

    @DeleteMapping("/delete")
    public void deleteArtigoById(@RequestParam("Id") String id) {
        this.artigoService.deleteArtigoById(id);
    }

    @GetMapping("/asd")
    public List<Artigo> findByStatusAndDataGreaterThan(
            @RequestParam Integer status,
            @RequestParam LocalDateTime data) {
        return this.artigoService.findByStatusAndDataGreaterThan(status, data);
    }

    @GetMapping("/periodo")
    public List<Artigo> obterArtigoPorDataHora(
            @RequestParam("de") LocalDateTime de,
            @RequestParam("ate") LocalDateTime ate
    ) {
        return this.artigoService.obterArtigoPorDataHora(de, ate);
    }


    @GetMapping("/artigo-complexo")
    public List<Artigo> encontraArtigosComplexo(@RequestParam("status") Integer status,
                                                @RequestParam("data") LocalDateTime data,
                                                @RequestParam("titulo") String titulo) {
        return this.artigoService.encontrarArtigosComplexosa(status, data, titulo);
    }

    @GetMapping("/pagina-artigos")
    public ResponseEntity<Page<Artigo>> listaArtigo(Pageable pageable) {
        Page<Artigo> artigos = this.artigoService.listaArtigo(pageable);
        return ResponseEntity.ok(artigos);
    }

    @GetMapping("/status-ordenado")
    public List<Artigo> findByStatusOrderByTituloAsc(@RequestParam("status") Integer status) {
        return this.artigoService.findByStatusOrderByTituloAsc(status);
    }

    @GetMapping("/status-query-ordenacao")
    public List<Artigo> obterArtigoPorStatusComOrdenacao(@RequestParam("status") Integer status) {
        return this.artigoService.obterArtigoPorStatusComOrdenacao(status);
    }

    //Busca por texto, foi necessario criar um indice dentro do mongodb
    //db.artigo.createIndex({texto: "text"})
    @GetMapping("/busca-texto")
    public List<Artigo> obterArtigoPorStatusComOrdenacao(@RequestParam("searchTerm") String termo) {
        return this.artigoService.findByTexto(termo);
    }

    //Contar artigos por status
    @GetMapping("/contar-artigo")
    public List<ArtigoStatusCount> contarArtigosPorStatus() {
        return this.artigoService.contarArtigosPorStatus();
    }

    //Contar artigos por status
    @GetMapping("/total-artigo-autor-periodo")
    public List<AutorTotalArtigo> calcularTotalArtigosPorAutorNoPeriodo(
            @RequestParam("dataInicio") LocalDate dataInicio,
            @RequestParam("dataFim") LocalDate dataFim
    ) {
        return this.artigoService.calcularTotalArtigosPorAutorNoPeriodo(dataInicio, dataFim);
    }

}
