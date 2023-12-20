package br.com.diefenthaeler.springblogmongodb.model;

import lombok.Data;

@Data
public class AutorTotalArtigo {

    private Autor autor;
    private Long totalArtigos;

}
