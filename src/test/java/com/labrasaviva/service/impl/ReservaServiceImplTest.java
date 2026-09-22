package com.labrasaviva.service.impl;

import com.labrasaviva.exception.MesaNoDisponibleException;
import com.labrasaviva.model.domain.Reserva;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {

    @InjectMocks
    private ReservaServiceImpl reservaService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void debeCrearReservaExitosamente() {
        Reserva r1 = new Reserva();
        r1.setIdMesa(1L);
        r1.setCliente("Carlos");
        r1.setFechaHora(LocalDateTime.now().plusDays(1));
        r1.setComensales(4);

        Reserva creada = reservaService.crearReserva(r1);

        assertNotNull(creada.getId());
        assertEquals("Carlos", creada.getCliente());
    }

    @Test
    void debeLanzarExcepcionSiMesaOcupada() {
        LocalDateTime fecha = LocalDateTime.now().plusDays(1);
        
        Reserva r1 = new Reserva();
        r1.setIdMesa(1L);
        r1.setCliente("Carlos");
        r1.setFechaHora(fecha);
        reservaService.crearReserva(r1);

        Reserva r2 = new Reserva();
        r2.setIdMesa(1L);
        r2.setCliente("Juan");
        r2.setFechaHora(fecha.plusHours(1)); // Menos de 2 horas de diferencia

        assertThrows(MesaNoDisponibleException.class, () -> {
            reservaService.crearReserva(r2);
        });
    }
}
