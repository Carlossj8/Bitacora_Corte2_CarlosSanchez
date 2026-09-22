package com.labrasaviva.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PedidoRequestDTO {
    
    @NotNull(message = "El ID de la mesa es obligatorio")
    private Long idMesa;
}
