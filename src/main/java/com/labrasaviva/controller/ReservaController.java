package com.labrasaviva.controller;

import com.labrasaviva.dto.request.ReservaRequestDTO;
import com.labrasaviva.dto.response.ReservaResponseDTO;
import com.labrasaviva.mapper.in.ReservaMapperIn;
import com.labrasaviva.mapper.out.ReservaMapperOut;
import com.labrasaviva.model.domain.Reserva;
import com.labrasaviva.service.ReservaService;
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
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Operaciones sobre las reservas de mesas")
public class ReservaController {

    private final ReservaService reservaService;
    private final ReservaMapperIn mapperIn;
    private final ReservaMapperOut mapperOut;

    @Operation(summary = "Crear una reserva")
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crearReserva(@Valid @RequestBody ReservaRequestDTO request) {
        log.info("Petición para crear reserva a nombre de {}", request.getCliente());
        Reserva reserva = mapperIn.toDomain(request);
        Reserva nuevaReserva = reservaService.crearReserva(reserva);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapperOut.toResponseDTO(nuevaReserva));
    }

    @Operation(summary = "Obtener todas las reservas")
    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> obtenerTodas() {
        log.info("Petición para obtener todas las reservas");
        List<Reserva> reservas = reservaService.obtenerTodas();
        return ResponseEntity.ok(reservas.stream()
                .map(mapperOut::toResponseDTO)
                .collect(Collectors.toList()));
    }
}
