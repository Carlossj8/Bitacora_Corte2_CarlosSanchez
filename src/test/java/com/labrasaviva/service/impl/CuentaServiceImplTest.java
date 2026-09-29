package com.labrasaviva.service.impl;

import com.labrasaviva.exception.MesaOcupadaException;
import com.labrasaviva.exception.MontoInsuficienteException;
import com.labrasaviva.exception.PedidosNoEntregadosException;
import com.labrasaviva.mapper.persistence.CuentaEntityMapper;
import com.labrasaviva.model.domain.Cuenta;
import com.labrasaviva.model.domain.EstadoCuenta;
import com.labrasaviva.model.domain.EstadoPedido;
import com.labrasaviva.model.domain.MedioPago;
import com.labrasaviva.model.domain.Pedido;
import com.labrasaviva.model.entity.CuentaEntity;
import com.labrasaviva.repository.CuentaRepository;
import com.labrasaviva.service.EventoAuditoriaService;
import com.labrasaviva.service.PedidoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuentaServiceImplTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private CuentaEntityMapper cuentaMapper;

    @Mock
    private PedidoService pedidoService;

    @Mock
    private EventoAuditoriaService eventoAuditoriaService;

    @InjectMocks
    private CuentaServiceImpl cuentaService;

    @Test
    void debeAbrirCuentaExitosamente() {
        CuentaEntity entity = CuentaEntity.builder()
                .id(1L)
                .idMesa(10L)
                .estado(EstadoCuenta.ABIERTA)
                .fechaApertura(LocalDateTime.now())
                .total(0.0)
                .build();

        Cuenta domain = new Cuenta();
        domain.setId(1L);
        domain.setIdMesa(10L);
        domain.setEstado(EstadoCuenta.ABIERTA);

        when(cuentaRepository.existsByIdMesaAndEstado(10L, EstadoCuenta.ABIERTA)).thenReturn(false);
        when(cuentaRepository.save(any(CuentaEntity.class))).thenReturn(entity);
        when(cuentaMapper.toDomain(entity)).thenReturn(domain);

        Cuenta cuenta = cuentaService.abrirCuenta(10L);

        assertNotNull(cuenta.getId());
        assertEquals(10L, cuenta.getIdMesa());
        assertEquals(EstadoCuenta.ABIERTA, cuenta.getEstado());
        verify(cuentaRepository).save(any());
        verify(eventoAuditoriaService).registrarEvento(eq("CUENTA_ABIERTA"), eq("Cuenta"), eq(1L), any(), any(), any());
    }

    @Test
    void debeLanzarExcepcionSiMesaOcupada() {
        when(cuentaRepository.existsByIdMesaAndEstado(10L, EstadoCuenta.ABIERTA)).thenReturn(true);

        assertThrows(MesaOcupadaException.class, () -> cuentaService.abrirCuenta(10L));
    }

    @Test
    void debeLanzarExcepcionSiHayPedidosNoEntregados() {
        CuentaEntity cuentaActiva = CuentaEntity.builder()
                .id(1L)
                .idMesa(2L)
                .estado(EstadoCuenta.ABIERTA)
                .build();

        Pedido pedidoPendiente = new Pedido();
        pedidoPendiente.setId(101L);
        pedidoPendiente.setEstado(EstadoPedido.EN_PREPARACION); // No entregado

        when(cuentaRepository.findByIdMesaAndEstado(2L, EstadoCuenta.ABIERTA)).thenReturn(Optional.of(cuentaActiva));
        when(pedidoService.obtenerPedidosPorMesa(2L)).thenReturn(List.of(pedidoPendiente));

        assertThrows(PedidosNoEntregadosException.class, () -> {
            cuentaService.cerrarCuenta(2L, MedioPago.EFECTIVO, 50000.0);
        });
    }

    @Test
    void debeLanzarExcepcionSiMontoInsuficiente() {
        CuentaEntity cuentaActiva = CuentaEntity.builder()
                .id(1L)
                .idMesa(3L)
                .estado(EstadoCuenta.ABIERTA)
                .build();

        Pedido pedidoEntregado = new Pedido();
        pedidoEntregado.setId(102L);
        pedidoEntregado.setEstado(EstadoPedido.ENTREGADO);
        pedidoEntregado.setTotal(80000.0);

        when(cuentaRepository.findByIdMesaAndEstado(3L, EstadoCuenta.ABIERTA)).thenReturn(Optional.of(cuentaActiva));
        when(pedidoService.obtenerPedidosPorMesa(3L)).thenReturn(List.of(pedidoEntregado));

        assertThrows(MontoInsuficienteException.class, () -> {
            cuentaService.cerrarCuenta(3L, MedioPago.EFECTIVO, 50000.0); // Menor a 80000
        });
    }

    @Test
    void debeCerrarCuentaExitosamente() {
        CuentaEntity cuentaActiva = CuentaEntity.builder()
                .id(1L)
                .idMesa(4L)
                .estado(EstadoCuenta.ABIERTA)
                .build();

        Pedido pedidoEntregado = new Pedido();
        pedidoEntregado.setId(103L);
        pedidoEntregado.setEstado(EstadoPedido.ENTREGADO);
        pedidoEntregado.setTotal(45000.0);

        Cuenta domainCerrada = new Cuenta();
        domainCerrada.setId(1L);
        domainCerrada.setTotal(45000.0);
        domainCerrada.setEstado(EstadoCuenta.CERRADA);

        when(cuentaRepository.findByIdMesaAndEstado(4L, EstadoCuenta.ABIERTA)).thenReturn(Optional.of(cuentaActiva));
        when(pedidoService.obtenerPedidosPorMesa(4L)).thenReturn(List.of(pedidoEntregado));
        when(cuentaRepository.save(any(CuentaEntity.class))).thenReturn(cuentaActiva);
        when(cuentaMapper.toDomain(any(CuentaEntity.class))).thenReturn(domainCerrada);

        Cuenta cuentaCerrada = cuentaService.cerrarCuenta(4L, MedioPago.TARJETA, 50000.0);

        assertEquals(EstadoCuenta.CERRADA, cuentaCerrada.getEstado());
        assertEquals(45000.0, cuentaCerrada.getTotal());
        verify(eventoAuditoriaService).registrarEvento(eq("CUENTA_CERRADA"), eq("Cuenta"), eq(1L), any(), any(), any());
    }
}
