package com.labrasaviva.model.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Reserva {
    private Long id;
    private Long idMesa;
    private String cliente;
    private LocalDateTime fechaHora;
    private Integer comensales;
}