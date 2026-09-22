package com.labrasaviva.controller;

import com.labrasaviva.dto.response.PlatoResponseDTO;
import com.labrasaviva.mapper.out.PlatoMapperOut;
import com.labrasaviva.model.domain.Plato;
import com.labrasaviva.service.PlatoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Menú", description = "Consulta pública de la carta del restaurante")
public class MenuController {

    private final PlatoService platoService;
    private final PlatoMapperOut mapperOut;

    @Operation(summary = "Consultar carta", description = "Devuelve la lista de platos que se encuentran actualmente disponibles en el restaurante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carta consultada exitosamente")
    })
    @GetMapping
    public ResponseEntity<List<PlatoResponseDTO>> consultarCarta() {
        log.info("Recibida petición para consultar la carta (solo platos disponibles)");
        List<Plato> platosDisponibles = platoService.obtenerDisponibles();
        List<PlatoResponseDTO> responseDTOs = platosDisponibles.stream()
                .map(mapperOut::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }
}
