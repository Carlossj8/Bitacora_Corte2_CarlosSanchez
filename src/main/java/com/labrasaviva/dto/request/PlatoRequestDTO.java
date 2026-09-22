package com.labrasaviva.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PlatoRequestDTO {
    
    @NotBlank(message = "El nombre del plato no puede estar vacío")
    private String nombre;
    
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    private Double precio;
    
    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;
    
    @NotNull(message = "Debe especificar si el plato está disponible")
    private Boolean disponible;
}
