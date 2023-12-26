package br.com.diefenthaeler.springblogmongodb.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Data
public class Artigo {

    @Id
    private String codigo;

    @NotBlank(message = "O titulo do artigo nao pode estar em branco!")
    private String titulo;

    @NotNull(message = "A data do artigo nao pode ser nula.")
    private LocalDateTime data;

    @NotBlank(message = "O texto do artigo nao pode estar em branco!")
    @TextIndexed
    private String texto;
    private String url;

    @NotNull(message = "O status do artigo nao pode ser nulo.")
    private Integer status;
    @DBRef
    private Autor autor;

    //garantir que o nosso documento esteja na mesma versao
    //garantir a concorrencia da aplicacao
    @Version
    private Long version;

}

