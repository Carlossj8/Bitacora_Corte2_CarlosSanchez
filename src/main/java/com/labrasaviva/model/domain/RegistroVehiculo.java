package com.labrasaviva.model.domain;
import lombok.Data;

import java.time.LocalDateTime;
@Data

public class RegistroVehiculo {
    private Long id;
    private String placa;
    private LocalDateTime entrada;
    private LocalDateTime salida;
    private Double cobro;
}