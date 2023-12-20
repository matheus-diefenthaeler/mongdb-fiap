package br.com.diefenthaeler.springblogmongodb.controller;

import br.com.diefenthaeler.springblogmongodb.model.Autor;
import br.com.diefenthaeler.springblogmongodb.service.AutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/v1/autor")
@RequiredArgsConstructor
@RestController
public class AutorController {

    private final AutorService autorService;

    @PostMapping
    public Autor criar(@RequestBody Autor autor) {
        return this.autorService.criar(autor);
    }

    @GetMapping("/{codigo}")
    public Autor obterPorCodigo(@PathVariable String codigo) {
        return this.autorService.obterPorCodigo(codigo);
    }
}
