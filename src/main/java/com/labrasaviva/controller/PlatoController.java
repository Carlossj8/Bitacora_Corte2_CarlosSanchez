package com.labrasaviva.controller;

import com.labrasaviva.dto.request.PlatoRequestDTO;
import com.labrasaviva.dto.response.PlatoResponseDTO;
import com.labrasaviva.mapper.in.PlatoMapperIn;
import com.labrasaviva.mapper.out.PlatoMapperOut;
import com.labrasaviva.model.domain.Plato;
import com.labrasaviva.service.PlatoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/platos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Platos", description = "Gestión administrativa de los platos del restaurante")
public class PlatoController {

    private final PlatoService platoService;
    private final PlatoMapperIn mapperIn;
    private final PlatoMapperOut mapperOut;

    @Operation(summary = "Obtener todos los platos", description = "Devuelve la lista completa de platos registrados, sin importar si están disponibles o no")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de platos obtenida correctamente")
    })
    @GetMapping
    public ResponseEntity<List<PlatoResponseDTO>> obtenerTodos() {
        List<Plato> platos = platoService.obtenerTodos();
        List<PlatoResponseDTO> response = platos.stream()
                .map(mapperOut::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener un plato por ID", description = "Busca y devuelve un plato en específico por su identificador único")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plato encontrado"),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PlatoResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.info("Petición para obtener plato con ID: {}", id);
        Plato plato = platoService.obtenerPorId(id);
        return ResponseEntity.ok(mapperOut.toResponseDTO(plato));
    }

    @Operation(summary = "Crear un nuevo plato", description = "Registra un nuevo plato en la base de datos")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Plato creado exitosamente")
    })
    @PostMapping
    public ResponseEntity<PlatoResponseDTO> crear(@Valid @RequestBody PlatoRequestDTO dto) {
        log.info("Petición para crear nuevo plato con nombre: {}", dto.getNombre());
        Plato plato = mapperIn.toDomain(dto);
        Plato creado = platoService.crear(plato);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapperOut.toResponseDTO(creado));
    }

    @Operation(summary = "Actualizar un plato", description = "Reemplaza los datos de un plato existente por los provistos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plato actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado para actualizar")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PlatoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody PlatoRequestDTO dto) {
        Plato plato = mapperIn.toDomain(dto);
        Plato actualizado = platoService.actualizar(id, plato);
        return ResponseEntity.ok(mapperOut.toResponseDTO(actualizado));
    }

    @Operation(summary = "Cambiar disponibilidad de un plato", description = "Actualiza únicamente el estado de disponibilidad de un plato")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada"),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado")
    })
    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<PlatoResponseDTO> cambiarDisponibilidad(@PathVariable Long id, @RequestParam Boolean disponible) {
        Plato actualizado = platoService.cambiarDisponibilidad(id, disponible);
        return ResponseEntity.ok(mapperOut.toResponseDTO(actualizado));
    }

    @Operation(summary = "Eliminar un plato", description = "Elimina físicamente un plato por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Plato eliminado exitosamente")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("Petición para eliminar plato con ID: {}", id);
        platoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
