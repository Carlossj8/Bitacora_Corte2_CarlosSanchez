package com.labrasaviva.service;

import com.labrasaviva.model.domain.Plato;
import java.util.List;

public interface PlatoService {
    List<Plato> obtenerTodos();
    List<Plato> obtenerDisponibles();
    Plato obtenerPorId(Long id);
    Plato crear(Plato plato);
    Plato actualizar(Long id, Plato plato);
    void eliminar(Long id);
    Plato cambiarDisponibilidad(Long id, Boolean disponible);
}
