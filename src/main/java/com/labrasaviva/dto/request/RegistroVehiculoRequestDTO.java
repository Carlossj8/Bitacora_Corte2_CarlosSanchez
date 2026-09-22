package com.labrasaviva.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegistroVehiculoRequestDTO {

    @NotBlank(message = "La placa del vehículo es obligatoria")
    private String placa;
}
