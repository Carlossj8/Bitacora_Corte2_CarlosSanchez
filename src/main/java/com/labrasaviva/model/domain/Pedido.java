package com.labrasaviva.model.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Pedido {
    private Long id;
    private Long idMesa;
    private EstadoPedido estado;
    private List<ItemPedido> items = new ArrayList<>();
    private Double total = 0.0;
}
