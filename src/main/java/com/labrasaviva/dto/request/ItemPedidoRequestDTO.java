package com.labrasaviva.dto.request;

import com.labrasaviva.model.domain.TerminoCoccion;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ItemPedidoRequestDTO {
    
    @NotNull(message = "El ID del plato es obligatorio")
    private Long idPlato;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;
    
    private TerminoCoccion terminoCoccion;
    
    private String observaciones;
}
