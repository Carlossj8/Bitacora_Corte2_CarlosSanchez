package com.labrasaviva.service.impl;

import com.labrasaviva.exception.PlatoNoEncontradoException;
import com.labrasaviva.exception.PlatoYaExisteException;
import com.labrasaviva.model.domain.Plato;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlatoServiceImplTest {

    private PlatoServiceImpl platoService;

    @BeforeEach
    void setUp() {
        platoService = new PlatoServiceImpl();
    }

    @Test
    void debeCrearPlatoExitosamente() {
        Plato nuevoPlato = new Plato();
        nuevoPlato.setNombre("Hamburguesa");
        nuevoPlato.setPrecio(25000.0);
        nuevoPlato.setCategoria("Comidas Rápidas");
        nuevoPlato.setDisponible(true);

        Plato platoCreado = platoService.crear(nuevoPlato);

        assertNotNull(platoCreado.getId());
        assertEquals("Hamburguesa", platoCreado.getNombre());
        
        List<Plato> platos = platoService.obtenerTodos();
        assertTrue(platos.stream().anyMatch(p -> p.getNombre().equals("Hamburguesa")));
    }

    @Test
    void debeLanzarExcepcionCuandoPlatoYaExiste() {
        // En setUp(), el servicio ya inicializa 3 platos de prueba.
        // Uno de ellos se llama "Punta de Anca"
        
        Plato platoDuplicado = new Plato();
        platoDuplicado.setNombre("Punta de Anca"); // Mismo nombre
        platoDuplicado.setPrecio(30000.0);

        assertThrows(PlatoYaExisteException.class, () -> {
            platoService.crear(platoDuplicado);
        });
    }

    @Test
    void debeLanzarExcepcionAlObtenerPlatoNoExistente() {
        assertThrows(PlatoNoEncontradoException.class, () -> {
            platoService.obtenerPorId(999L);
        });
    }

    @Test
    void debeActualizarPlatoExitosamente() {
        // Plato 1 existe por defecto con ID 1
        Plato actualizado = new Plato();
        actualizado.setNombre("Ajiaco Santafereño");
        actualizado.setPrecio(32000.0);
        actualizado.setCategoria("Sopas");
        actualizado.setDisponible(true);

        Plato resultado = platoService.actualizar(1L, actualizado);

        assertEquals("Ajiaco Santafereño", resultado.getNombre());
        assertEquals(32000.0, resultado.getPrecio());
    }
}
