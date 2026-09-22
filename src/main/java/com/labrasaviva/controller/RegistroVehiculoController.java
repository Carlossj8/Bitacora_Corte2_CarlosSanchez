package com.labrasaviva.controller;

import com.labrasaviva.dto.request.RegistroVehiculoRequestDTO;
import com.labrasaviva.dto.response.RegistroVehiculoResponseDTO;
import com.labrasaviva.mapper.out.RegistroVehiculoMapperOut;
import com.labrasaviva.model.domain.RegistroVehiculo;
import com.labrasaviva.service.RegistroVehiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/vehiculos")
@RequiredArgsConstructor
@Tag(name = "Vehículos", description = "Operaciones del parqueadero")
public class RegistroVehiculoController {

    private final RegistroVehiculoService vehiculoService;
    private final RegistroVehiculoMapperOut mapperOut;

    @Operation(summary = "Registrar entrada de un vehículo")
    @PostMapping("/entrada")
    public ResponseEntity<RegistroVehiculoResponseDTO> registrarEntrada(@Valid @RequestBody RegistroVehiculoRequestDTO request) {
        log.info("Petición para registrar entrada del vehículo {}", request.getPlaca());
        RegistroVehiculo registro = vehiculoService.registrarEntrada(request.getPlaca());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapperOut.toResponseDTO(registro));
    }

    @Operation(summary = "Registrar salida de un vehículo y calcular cobro")
    @PostMapping("/salida")
    public ResponseEntity<RegistroVehiculoResponseDTO> registrarSalida(@Valid @RequestBody RegistroVehiculoRequestDTO request) {
        log.info("Petición para registrar salida del vehículo {}", request.getPlaca());
        RegistroVehiculo registro = vehiculoService.registrarSalida(request.getPlaca());
        return ResponseEntity.ok(mapperOut.toResponseDTO(registro));
    }

    @Operation(summary = "Obtener todos los registros")
    @GetMapping
    public ResponseEntity<List<RegistroVehiculoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(vehiculoService.obtenerTodos().stream()
                .map(mapperOut::toResponseDTO)
                .collect(Collectors.toList()));
    }
}
