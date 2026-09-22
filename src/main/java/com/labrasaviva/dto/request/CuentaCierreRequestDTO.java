package com.labrasaviva.dto.request;

import com.labrasaviva.model.domain.MedioPago;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CuentaCierreRequestDTO {
    
    @NotNull(message = "El medio de pago es obligatorio")
    private MedioPago medioPago;
    
    @NotNull(message = "El monto recibido es obligatorio")
    @PositiveOrZero(message = "El monto recibido no puede ser negativo")
    private Double montoRecibido;
}
