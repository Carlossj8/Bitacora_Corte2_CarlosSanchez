package com.labrasaviva.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservaResponseDTO {
    private Long id;
    private Long idMesa;
    private String cliente;
    private LocalDateTime fechaHora;
    private Integer comensales;
}
