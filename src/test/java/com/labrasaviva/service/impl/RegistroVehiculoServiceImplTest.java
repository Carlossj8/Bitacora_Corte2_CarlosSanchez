package com.labrasaviva.service.impl;

import com.labrasaviva.exception.VehiculoNoEncontradoException;
import com.labrasaviva.exception.VehiculoYaRegistradoException;
import com.labrasaviva.model.domain.RegistroVehiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RegistroVehiculoServiceImplTest {

    @InjectMocks
    private RegistroVehiculoServiceImpl vehiculoService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void debeRegistrarEntradaExitosamente() {
        RegistroVehiculo registro = vehiculoService.registrarEntrada("ABC-123");
        assertNotNull(registro.getId());
        assertEquals("ABC-123", registro.getPlaca());
        assertNotNull(registro.getEntrada());
        assertNull(registro.getSalida());
    }

    @Test
    void debeLanzarExcepcionAlRegistrarEntradaDuplicada() {
        vehiculoService.registrarEntrada("ABC-123");
        assertThrows(VehiculoYaRegistradoException.class, () -> {
            vehiculoService.registrarEntrada("ABC-123");
        });
    }

    @Test
    void debeRegistrarSalidaConCobroExitosamente() {
        vehiculoService.registrarEntrada("ABC-123");
        
        RegistroVehiculo salida = vehiculoService.registrarSalida("ABC-123");
        
        assertNotNull(salida.getSalida());
        assertNotNull(salida.getCobro());
        assertEquals(5000.0, salida.getCobro()); // Cobro mínimo de 1 hora
    }

    @Test
    void debeLanzarExcepcionSiSalidaNoExiste() {
        assertThrows(VehiculoNoEncontradoException.class, () -> {
            vehiculoService.registrarSalida("XYZ-999");
        });
    }
}
