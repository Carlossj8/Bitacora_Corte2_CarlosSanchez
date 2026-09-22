package com.labrasaviva.dto.response;

import com.labrasaviva.model.domain.TerminoCoccion;
import lombok.Data;

@Data
public class ItemPedidoResponseDTO {
    private Long id;
    private Long idPlato;
    private String nombrePlato;
    private Double precioCongelado;
    private Integer cantidad;
    private TerminoCoccion terminoCoccion;
    private String observaciones;
    private Double subtotal;
}
