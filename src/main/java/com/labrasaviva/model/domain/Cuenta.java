package com.labrasaviva.model.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data

public class Cuenta {
    private Long id;
    private Long idMesa;
    private Double total;
    private EstadoCuenta estado;
    private LocalDateTime fechaApertura;
}