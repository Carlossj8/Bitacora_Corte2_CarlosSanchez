package com.labrasaviva.controller;

import com.labrasaviva.dto.response.EventoResponseDTO;
import com.labrasaviva.mapper.out.EventoMapperOut;
import com.labrasaviva.service.EventoAuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auditoria/eventos")
@RequiredArgsConstructor
@Tag(name = "Auditoría NoSQL", description = "Endpoints para consulta de la bitácora de eventos almacenada en MongoDB")
public class EventoAuditoriaController {

    private final EventoAuditoriaService eventoAuditoriaService;
    private final EventoMapperOut eventoMapperOut;

    @GetMapping
    @Operation(summary = "Listar todos los eventos de auditoría registrados en MongoDB")
    public ResponseEntity<List<EventoResponseDTO>> listarEventos() {
        List<EventoResponseDTO> eventos = eventoAuditoriaService.listarTodos().stream()
                .map(eventoMapperOut::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(eventos);
    }

    @GetMapping("/{entidadTipo}/{entidadId}")
    @Operation(summary = "Listar eventos de auditoría por tipo de entidad e ID")
    public ResponseEntity<List<EventoResponseDTO>> listarPorEntidad(
            @PathVariable String entidadTipo,
            @PathVariable Long entidadId) {
        List<EventoResponseDTO> eventos = eventoAuditoriaService.listarPorEntidad(entidadTipo, entidadId).stream()
                .map(eventoMapperOut::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(eventos);
    }
}
