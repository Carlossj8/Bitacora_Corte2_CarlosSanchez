package com.labrasaviva.service.impl;

import com.labrasaviva.mapper.persistence.EventoMapper;
import com.labrasaviva.model.document.EventoRestauranteDocument;
import com.labrasaviva.model.domain.EventoRestaurante;
import com.labrasaviva.repository.EventoRestauranteRepository;
import com.labrasaviva.service.EventoAuditoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventoAuditoriaServiceImpl implements EventoAuditoriaService {

    private final EventoRestauranteRepository eventoRepository;
    private final EventoMapper eventoMapper;

    @Override
    public void registrarEvento(String tipo, String entidadTipo, Long entidadId, String descripcion, String usuario, Map<String, Object> metadatos) {
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                EventoRestaurante evento = EventoRestaurante.builder()
                        .tipo(tipo)
                        .entidadTipo(entidadTipo)
                        .entidadId(entidadId)
                        .descripcion(descripcion)
                        .usuario(usuario != null ? usuario : "SISTEMA")
                        .timestamp(LocalDateTime.now())
                        .metadatos(metadatos)
                        .build();

                EventoRestauranteDocument doc = eventoMapper.toDocument(evento);
                eventoRepository.save(doc);
                log.info("Evento NoSQL registrado en MongoDB [tipo={}, entidad={}, id={}]", tipo, entidadTipo, entidadId);
            } catch (Exception e) {
                log.warn("No se pudo registrar evento en MongoDB (cluster no conectado o no configurado): {}", e.getMessage());
            }
        });
    }

    @Override
    public List<EventoRestaurante> listarTodos() {
        try {
            return eventoRepository.findAllByOrderByTimestampDesc().stream()
                    .map(eventoMapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Error al consultar eventos en MongoDB: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<EventoRestaurante> listarPorEntidad(String entidadTipo, Long entidadId) {
        try {
            return eventoRepository.findByEntidadTipoAndEntidadId(entidadTipo, entidadId).stream()
                    .map(eventoMapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Error al consultar eventos por entidad en MongoDB: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
