package com.labrasaviva.service.impl;

import com.labrasaviva.exception.VehiculoNoEncontradoException;
import com.labrasaviva.exception.VehiculoYaRegistradoException;
import com.labrasaviva.mapper.persistence.RegistroVehiculoEntityMapper;
import com.labrasaviva.model.domain.RegistroVehiculo;
import com.labrasaviva.model.entity.RegistroVehiculoEntity;
import com.labrasaviva.repository.RegistroVehiculoRepository;
import com.labrasaviva.service.EventoAuditoriaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistroVehiculoServiceImplTest {

    @Mock
    private RegistroVehiculoRepository vehiculoRepository;

    @Mock
    private RegistroVehiculoEntityMapper vehiculoMapper;

    @Mock
    private EventoAuditoriaService eventoAuditoriaService;

    @InjectMocks
    private RegistroVehiculoServiceImpl vehiculoService;

    @Test
    void debeRegistrarEntradaExitosamente() {
        RegistroVehiculoEntity entity = RegistroVehiculoEntity.builder()
                .id(1L)
                .placa("ABC-123")
                .entrada(LocalDateTime.now())
                .build();

        RegistroVehiculo domain = new RegistroVehiculo();
        domain.setId(1L);
        domain.setPlaca("ABC-123");
        domain.setEntrada(LocalDateTime.now());

        when(vehiculoRepository.existsByPlacaIgnoreCaseAndSalidaIsNull("ABC-123")).thenReturn(false);
        when(vehiculoRepository.save(any(RegistroVehiculoEntity.class))).thenReturn(entity);
        when(vehiculoMapper.toDomain(entity)).thenReturn(domain);

        RegistroVehiculo registro = vehiculoService.registrarEntrada("ABC-123");

        assertNotNull(registro.getId());
        assertEquals("ABC-123", registro.getPlaca());
        assertNotNull(registro.getEntrada());
        assertNull(registro.getSalida());
        verify(eventoAuditoriaService).registrarEvento(eq("VEHICULO_INGRESADO"), eq("Vehiculo"), eq(1L), any(), any(), any());
    }

    @Test
    void debeLanzarExcepcionAlRegistrarEntradaDuplicada() {
        when(vehiculoRepository.existsByPlacaIgnoreCaseAndSalidaIsNull("ABC-123")).thenReturn(true);

        assertThrows(VehiculoYaRegistradoException.class, () -> vehiculoService.registrarEntrada("ABC-123"));
    }

    @Test
    void debeRegistrarSalidaConCobroExitosamente() {
        RegistroVehiculoEntity activo = RegistroVehiculoEntity.builder()
                .id(1L)
                .placa("ABC-123")
                .entrada(LocalDateTime.now().minusMinutes(30))
                .build();

        RegistroVehiculo domainSalida = new RegistroVehiculo();
        domainSalida.setId(1L);
        domainSalida.setPlaca("ABC-123");
        domainSalida.setSalida(LocalDateTime.now());
        domainSalida.setCobro(5000.0);

        when(vehiculoRepository.findByPlacaIgnoreCaseAndSalidaIsNull("ABC-123")).thenReturn(Optional.of(activo));
        when(vehiculoRepository.save(any(RegistroVehiculoEntity.class))).thenReturn(activo);
        when(vehiculoMapper.toDomain(any(RegistroVehiculoEntity.class))).thenReturn(domainSalida);

        RegistroVehiculo salida = vehiculoService.registrarSalida("ABC-123");

        assertNotNull(salida.getSalida());
        assertNotNull(salida.getCobro());
        assertEquals(5000.0, salida.getCobro()); // Cobro mínimo de 1 hora
        verify(eventoAuditoriaService).registrarEvento(eq("VEHICULO_SALIDA"), eq("Vehiculo"), eq(1L), any(), any(), any());
    }

    @Test
    void debeLanzarExcepcionSiSalidaNoExiste() {
        when(vehiculoRepository.findByPlacaIgnoreCaseAndSalidaIsNull("XYZ-999")).thenReturn(Optional.empty());

        assertThrows(VehiculoNoEncontradoException.class, () -> vehiculoService.registrarSalida("XYZ-999"));
    }
}
