package com.labrasaviva.service.impl;

import com.labrasaviva.exception.PlatoNoEncontradoException;
import com.labrasaviva.exception.PlatoYaExisteException;
import com.labrasaviva.model.domain.Plato;
import com.labrasaviva.service.PlatoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PlatoServiceImpl implements PlatoService {

    private final List<Plato> platos = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong(1);

    public PlatoServiceImpl() {
        // Inicializando datos de prueba
        Plato p1 = new Plato();
        p1.setId(counter.getAndIncrement());
        p1.setNombre("Punta de Anca");
        p1.setPrecio(45000.0);
        p1.setCategoria("Cortes");
        p1.setDisponible(true);

        Plato p2 = new Plato();
        p2.setId(counter.getAndIncrement());
        p2.setNombre("Pechuga a la Plancha");
        p2.setPrecio(25000.0);
        p2.setCategoria("Aves");
        p2.setDisponible(true);

        Plato p3 = new Plato();
        p3.setId(counter.getAndIncrement());
        p3.setNombre("Churrasco Especial");
        p3.setPrecio(50000.0);
        p3.setCategoria("Cortes");
        p3.setDisponible(false); // No disponible por ahora

        platos.add(p1);
        platos.add(p2);
        platos.add(p3);
    }

    @Override
    public List<Plato> obtenerTodos() {
        log.info("Consultando todos los platos de la base de datos simulada. Total: {}", platos.size());
        return platos;
    }

    @Override
    public List<Plato> obtenerDisponibles() {
        return platos.stream()
                .filter(Plato::getDisponible)
                .collect(Collectors.toList());
    }

    @Override
    public Plato obtenerPorId(Long id) {
        return Optional.ofNullable(platos.stream()
                        .filter(p -> p.getId().equals(id))
                        .findFirst()
                        .orElse(null))
                .orElseThrow(() -> new PlatoNoEncontradoException("Plato no encontrado con ID: " + id));
    }

    private boolean nombreExiste(String nombre) {
        return platos.stream()
                .anyMatch(p -> p.getNombre().equalsIgnoreCase(nombre));
    }

    @Override
    public Plato crear(Plato plato) {
        if (nombreExiste(plato.getNombre())) {
            throw new PlatoYaExisteException("Ya existe un plato con el nombre: " + plato.getNombre());
        }
        plato.setId(counter.getAndIncrement());
        platos.add(plato);
        log.info("Se ha creado un nuevo plato: {} con ID {}", plato.getNombre(), plato.getId());
        return plato;
    }

    @Override
    public Plato actualizar(Long id, Plato platoActualizado) {
        Plato plato = obtenerPorId(id); // Lanzará excepción si no existe
        
        // Si cambia el nombre, verificar que no colisione con otro
        if (!plato.getNombre().equalsIgnoreCase(platoActualizado.getNombre()) && nombreExiste(platoActualizado.getNombre())) {
            throw new PlatoYaExisteException("Ya existe otro plato con el nombre: " + platoActualizado.getNombre());
        }

        plato.setNombre(platoActualizado.getNombre());
        plato.setPrecio(platoActualizado.getPrecio());
        plato.setCategoria(platoActualizado.getCategoria());
        plato.setDisponible(platoActualizado.getDisponible());
        return plato;
    }

    @Override
    public void eliminar(Long id) {
        Plato plato = obtenerPorId(id); // Validar existencia
        platos.remove(plato);
    }

    @Override
    public Plato cambiarDisponibilidad(Long id, Boolean disponible) {
        Plato plato = obtenerPorId(id); // Lanzará excepción si no existe
        plato.setDisponible(disponible);
        return plato;
    }
}
