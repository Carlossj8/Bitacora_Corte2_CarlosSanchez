package com.labrasaviva.dto.response;

import com.labrasaviva.model.domain.EstadoCuenta;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CuentaResponseDTO {
    private Long id;
    private Long idMesa;
    private Double total;
    private EstadoCuenta estado;
    private LocalDateTime fechaApertura;
    
    // Estos campos podrían llenarse solo al cerrar
    private String comprobantePago;
    private String numeroFactura;
}
