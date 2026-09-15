package com.labrasaviva.model.domain;

import lombok.Data;

@Data

public class ItemPedido {
    private Long id;
    private Long idPlato;
    private String nombrePlato;
    private Double precioCongelado;
    private Integer cantidad;
    private TerminoCoccion terminoCoccion;
}