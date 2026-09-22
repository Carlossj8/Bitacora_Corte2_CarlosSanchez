package com.labrasaviva.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservaRequestDTO {

    @NotNull(message = "El ID de la mesa es obligatorio")
    private Long idMesa;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String cliente;

    @NotNull(message = "La fecha y hora son obligatorias")
    @Future(message = "La fecha de reserva debe ser en el futuro")
    private LocalDateTime fechaHora;

    @NotNull(message = "El número de comensales es obligatorio")
    @Positive(message = "El número de comensales debe ser mayor a cero")
    private Integer comensales;
}
