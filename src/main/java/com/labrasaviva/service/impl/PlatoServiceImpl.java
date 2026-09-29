package com.labrasaviva.service.impl;

import com.labrasaviva.exception.PlatoNoEncontradoException;
import com.labrasaviva.exception.PlatoYaExisteException;
import com.labrasaviva.mapper.persistence.PlatoEntityMapper;
import com.labrasaviva.model.domain.Plato;
import com.labrasaviva.model.entity.PlatoEntity;
import com.labrasaviva.repository.PlatoRepository;
import com.labrasaviva.service.EventoAuditoriaService;
import com.labrasaviva.service.PlatoService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlatoServiceImpl implements PlatoService {

    private final PlatoRepository platoRepository;
    private final PlatoEntityMapper platoMapper;
    private final EventoAuditoriaService eventoAuditoriaService;

    @PostConstruct
    public void inicializarDatos() {
        if (platoRepository.count() == 0) {
            platoRepository.save(PlatoEntity.builder().nombre("Punta de Anca").precio(45000.0).categoria("Cortes").disponible(true).build());
            platoRepository.save(PlatoEntity.builder().nombre("Pechuga a la Plancha").precio(25000.0).categoria("Aves").disponible(true).build());
            platoRepository.save(PlatoEntity.builder().nombre("Churrasco Especial").precio(50000.0).categoria("Cortes").disponible(false).build());
            log.info("Datos iniciales de platos precargados en la base de datos relacional.");
        }
    }

    @Override
    public List<Plato> obtenerTodos() {
        return platoRepository.findAll().stream()
                .map(platoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Plato> obtenerDisponibles() {
        return platoRepository.findByDisponibleTrue().stream()
                .map(platoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Plato obtenerPorId(Long id) {
        return platoRepository.findById(id)
                .map(platoMapper::toDomain)
                .orElseThrow(() -> new PlatoNoEncontradoException("Plato no encontrado con ID: " + id));
    }

    @Override
    public Plato crear(Plato plato) {
        if (platoRepository.existsByNombreIgnoreCase(plato.getNombre())) {
            throw new PlatoYaExisteException("Ya existe un plato con el nombre: " + plato.getNombre());
        }

        PlatoEntity entity = platoMapper.toEntity(plato);
        PlatoEntity guardado = platoRepository.save(entity);
        log.info("Plato guardado en BD con ID: {}", guardado.getId());

        eventoAuditoriaService.registrarEvento(
                "PLATO_CREADO",
                "Plato",
                guardado.getId(),
                "Se creó el plato " + guardado.getNombre() + " en el catálogo",
                "ADMIN",
                Map.of("precio", guardado.getPrecio(), "categoria", guardado.getCategoria())
        );

        return platoMapper.toDomain(guardado);
    }

    @Override
    public Plato actualizar(Long id, Plato platoActualizado) {
        PlatoEntity entity = platoRepository.findById(id)
                .orElseThrow(() -> new PlatoNoEncontradoException("Plato no encontrado con ID: " + id));

        if (platoRepository.existsByNombreIgnoreCaseAndIdNot(platoActualizado.getNombre(), id)) {
            throw new PlatoYaExisteException("Ya existe otro plato con el nombre: " + platoActualizado.getNombre());
        }

        entity.setNombre(platoActualizado.getNombre());
        entity.setPrecio(platoActualizado.getPrecio());
        entity.setCategoria(platoActualizado.getCategoria());
        entity.setDisponible(platoActualizado.getDisponible());

        PlatoEntity guardado = platoRepository.save(entity);
        return platoMapper.toDomain(guardado);
    }

    @Override
    public void eliminar(Long id) {
        if (!platoRepository.existsById(id)) {
            throw new PlatoNoEncontradoException("Plato no encontrado con ID: " + id);
        }
        platoRepository.deleteById(id);
        log.info("Plato con ID {} eliminado de BD", id);
    }

    @Override
    public Plato cambiarDisponibilidad(Long id, Boolean disponible) {
        PlatoEntity entity = platoRepository.findById(id)
                .orElseThrow(() -> new PlatoNoEncontradoException("Plato no encontrado con ID: " + id));

        entity.setDisponible(disponible);
        PlatoEntity guardado = platoRepository.save(entity);

        if (!disponible) {
            eventoAuditoriaService.registrarEvento(
                    "PLATO_AGOTADO",
                    "Plato",
                    guardado.getId(),
                    "El plato " + guardado.getNombre() + " fue marcado como no disponible",
                    "COCINA",
                    Map.of("categoria", guardado.getCategoria())
            );
        }

        return platoMapper.toDomain(guardado);
    }
}
