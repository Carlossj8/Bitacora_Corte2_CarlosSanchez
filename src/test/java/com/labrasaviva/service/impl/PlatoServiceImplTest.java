package com.labrasaviva.service.impl;

import com.labrasaviva.exception.PlatoNoEncontradoException;
import com.labrasaviva.exception.PlatoYaExisteException;
import com.labrasaviva.mapper.persistence.PlatoEntityMapper;
import com.labrasaviva.model.domain.Plato;
import com.labrasaviva.model.entity.PlatoEntity;
import com.labrasaviva.repository.PlatoRepository;
import com.labrasaviva.service.EventoAuditoriaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlatoServiceImplTest {

    @Mock
    private PlatoRepository platoRepository;

    @Mock
    private PlatoEntityMapper platoMapper;

    @Mock
    private EventoAuditoriaService eventoAuditoriaService;

    @InjectMocks
    private PlatoServiceImpl platoService;

    @Test
    void debeCrearPlatoExitosamente() {
        Plato nuevoPlato = new Plato();
        nuevoPlato.setNombre("Hamburguesa");
        nuevoPlato.setPrecio(25000.0);
        nuevoPlato.setCategoria("Comidas Rápidas");
        nuevoPlato.setDisponible(true);

        PlatoEntity entity = PlatoEntity.builder()
                .id(1L)
                .nombre("Hamburguesa")
                .precio(25000.0)
                .categoria("Comidas Rápidas")
                .disponible(true)
                .build();

        when(platoRepository.existsByNombreIgnoreCase("Hamburguesa")).thenReturn(false);
        when(platoMapper.toEntity(nuevoPlato)).thenReturn(entity);
        when(platoRepository.save(entity)).thenReturn(entity);
        when(platoMapper.toDomain(entity)).thenReturn(nuevoPlato);
        nuevoPlato.setId(1L);

        Plato platoCreado = platoService.crear(nuevoPlato);

        assertNotNull(platoCreado.getId());
        assertEquals("Hamburguesa", platoCreado.getNombre());
        verify(platoRepository).save(any());
        verify(eventoAuditoriaService).registrarEvento(eq("PLATO_CREADO"), eq("Plato"), eq(1L), any(), any(), any());
    }

    @Test
    void debeLanzarExcepcionCuandoPlatoYaExiste() {
        Plato platoDuplicado = new Plato();
        platoDuplicado.setNombre("Punta de Anca");

        when(platoRepository.existsByNombreIgnoreCase("Punta de Anca")).thenReturn(true);

        assertThrows(PlatoYaExisteException.class, () -> platoService.crear(platoDuplicado));
    }

    @Test
    void debeLanzarExcepcionAlObtenerPlatoNoExistente() {
        when(platoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PlatoNoEncontradoException.class, () -> platoService.obtenerPorId(999L));
    }

    @Test
    void debeActualizarPlatoExitosamente() {
        PlatoEntity existente = PlatoEntity.builder()
                .id(1L)
                .nombre("Ajiaco")
                .precio(30000.0)
                .categoria("Sopas")
                .disponible(true)
                .build();

        Plato actualizado = new Plato();
        actualizado.setNombre("Ajiaco Santafereño");
        actualizado.setPrecio(32000.0);
        actualizado.setCategoria("Sopas");
        actualizado.setDisponible(true);

        when(platoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(platoRepository.existsByNombreIgnoreCaseAndIdNot("Ajiaco Santafereño", 1L)).thenReturn(false);
        when(platoRepository.save(any(PlatoEntity.class))).thenReturn(existente);
        when(platoMapper.toDomain(any(PlatoEntity.class))).thenReturn(actualizado);

        Plato resultado = platoService.actualizar(1L, actualizado);

        assertEquals("Ajiaco Santafereño", resultado.getNombre());
        assertEquals(32000.0, resultado.getPrecio());
    }
}
