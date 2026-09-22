package com.labrasaviva.dto.response;

import lombok.Data;

@Data
public class PlatoResponseDTO {
    private Long id;
    private String nombre;
    private Double precio;
    private String categoria;
    private Boolean disponible;
}
