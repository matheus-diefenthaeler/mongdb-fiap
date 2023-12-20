package br.com.diefenthaeler.springblogmongodb.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Autor {

    @Id
    private String codigo;
    private String nome;
    private String biografia;
    private String imagem;

}
