package com.labrasaviva.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegistroVehiculoResponseDTO {
    private Long id;
    private String placa;
    private LocalDateTime entrada;
    private LocalDateTime salida;
    private Double cobro;
}
