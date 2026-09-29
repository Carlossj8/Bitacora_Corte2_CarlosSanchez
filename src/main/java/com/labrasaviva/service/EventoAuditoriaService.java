package com.labrasaviva.service;

import com.labrasaviva.model.domain.EventoRestaurante;

import java.util.List;
import java.util.Map;

public interface EventoAuditoriaService {

    void registrarEvento(String tipo, String entidadTipo, Long entidadId, String descripcion, String usuario, Map<String, Object> metadatos);

    List<EventoRestaurante> listarTodos();

    List<EventoRestaurante> listarPorEntidad(String entidadTipo, Long entidadId);
}
