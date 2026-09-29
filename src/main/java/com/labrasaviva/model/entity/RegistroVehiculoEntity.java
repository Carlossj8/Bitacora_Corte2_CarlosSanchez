package com.labrasaviva.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "registros_vehiculo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroVehiculoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String placa;

    @Column(nullable = false)
    private LocalDateTime entrada;

    private LocalDateTime salida;

    private Double cobro;
}
