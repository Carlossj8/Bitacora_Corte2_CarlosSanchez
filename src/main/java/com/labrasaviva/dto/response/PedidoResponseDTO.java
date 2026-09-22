package com.labrasaviva.dto.response;

import com.labrasaviva.model.domain.EstadoPedido;
import lombok.Data;
import java.util.List;

@Data
public class PedidoResponseDTO {
    private Long id;
    private Long idMesa;
    private EstadoPedido estado;
    private List<ItemPedidoResponseDTO> items;
    private Double total;
}
