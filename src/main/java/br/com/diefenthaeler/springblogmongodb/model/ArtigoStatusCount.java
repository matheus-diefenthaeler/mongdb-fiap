package br.com.diefenthaeler.springblogmongodb.model;

import lombok.Data;

@Data
public class ArtigoStatusCount {
    private Integer status;
    private Long quantidade;
}
