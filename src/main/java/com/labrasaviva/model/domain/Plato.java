package main.java.com.labrasaviva.model.domain;

import lombok.Data;
@Data


public class Plato {
    private Long id;
    private String nombre;
    private Double precio;
    private String categoria;
    private Boolean disponible;
}