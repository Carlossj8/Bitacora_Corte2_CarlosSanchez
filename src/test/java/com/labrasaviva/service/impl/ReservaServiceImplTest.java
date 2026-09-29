package com.labrasaviva.service.impl;

import com.labrasaviva.exception.MesaNoDisponibleException;
import com.labrasaviva.mapper.persistence.ReservaEntityMapper;
import com.labrasaviva.model.domain.Reserva;
import com.labrasaviva.model.entity.ReservaEntity;
import com.labrasaviva.repository.ReservaRepository;
import com.labrasaviva.service.EventoAuditoriaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private ReservaEntityMapper reservaMapper;

    @Mock
    private EventoAuditoriaService eventoAuditoriaService;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    @Test
    void debeCrearReservaExitosamente() {
        LocalDateTime fecha = LocalDateTime.now().plusDays(1);
        Reserva r1 = new Reserva();
        r1.setIdMesa(1L);
        r1.setCliente("Carlos");
        r1.setFechaHora(fecha);
        r1.setComensales(4);

        ReservaEntity entity = ReservaEntity.builder()
                .id(1L)
                .idMesa(1L)
                .cliente("Carlos")
                .fechaHora(fecha)
                .comensales(4)
                .build();

        Reserva domain = new Reserva();
        domain.setId(1L);
        domain.setIdMesa(1L);
        domain.setCliente("Carlos");

        when(reservaRepository.findByIdMesa(1L)).thenReturn(List.of());
        when(reservaMapper.toEntity(r1)).thenReturn(entity);
        when(reservaRepository.save(entity)).thenReturn(entity);
        when(reservaMapper.toDomain(entity)).thenReturn(domain);

        Reserva creada = reservaService.crearReserva(r1);

        assertNotNull(creada.getId());
        assertEquals("Carlos", creada.getCliente());
        verify(eventoAuditoriaService).registrarEvento(eq("RESERVA_CREADA"), eq("Reserva"), eq(1L), any(), any(), any());
    }

    @Test
    void debeLanzarExcepcionSiMesaOcupada() {
        LocalDateTime fecha = LocalDateTime.now().plusDays(1);

        ReservaEntity existente = ReservaEntity.builder()
                .id(1L)
                .idMesa(1L)
                .cliente("Carlos")
                .fechaHora(fecha)
                .comensales(4)
                .build();

        Reserva r2 = new Reserva();
        r2.setIdMesa(1L);
        r2.setCliente("Juan");
        r2.setFechaHora(fecha.plusHours(1)); // Menos de 2 horas de diferencia

        when(reservaRepository.findByIdMesa(1L)).thenReturn(List.of(existente));

        assertThrows(MesaNoDisponibleException.class, () -> reservaService.crearReserva(r2));
    }
}
