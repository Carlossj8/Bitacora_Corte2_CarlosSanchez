package com.labrasaviva.service.impl;

import com.labrasaviva.exception.MesaOcupadaException;
import com.labrasaviva.exception.MontoInsuficienteException;
import com.labrasaviva.exception.PedidosNoEntregadosException;
import com.labrasaviva.model.domain.Cuenta;
import com.labrasaviva.model.domain.EstadoCuenta;
import com.labrasaviva.model.domain.EstadoPedido;
import com.labrasaviva.model.domain.MedioPago;
import com.labrasaviva.model.domain.Pedido;
import com.labrasaviva.service.PedidoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CuentaServiceImplTest {

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private CuentaServiceImpl cuentaService;

    @Test
    void debeAbrirCuentaExitosamente() {
        Cuenta cuenta = cuentaService.abrirCuenta(10L);
        assertNotNull(cuenta.getId());
        assertEquals(10L, cuenta.getIdMesa());
        assertEquals(EstadoCuenta.ABIERTA, cuenta.getEstado());
    }

    @Test
    void debeLanzarExcepcionSiMesaOcupada() {
        cuentaService.abrirCuenta(10L);
        
        assertThrows(MesaOcupadaException.class, () -> {
            cuentaService.abrirCuenta(10L);
        });
    }

    @Test
    void debeCerrarCuentaExitosamente() {
        cuentaService.abrirCuenta(10L);

        Pedido p1 = new Pedido();
        p1.setEstado(EstadoPedido.ENTREGADO);
        p1.setTotal(50000.0);

        Pedido p2 = new Pedido();
        p2.setEstado(EstadoPedido.ENTREGADO);
        p2.setTotal(30000.0);

        when(pedidoService.obtenerPedidosPorMesa(10L)).thenReturn(List.of(p1, p2));

        Cuenta cuentaCerrada = cuentaService.cerrarCuenta(10L, MedioPago.EFECTIVO, 80000.0);

        assertEquals(EstadoCuenta.CERRADA, cuentaCerrada.getEstado());
        assertEquals(80000.0, cuentaCerrada.getTotal());
    }

    @Test
    void debeLanzarExcepcionSiPedidosNoEntregados() {
        cuentaService.abrirCuenta(10L);

        Pedido p1 = new Pedido();
        p1.setEstado(EstadoPedido.ENTREGADO);

        Pedido p2 = new Pedido();
        p2.setEstado(EstadoPedido.EN_PREPARACION); // <--- Pendiente

        when(pedidoService.obtenerPedidosPorMesa(10L)).thenReturn(List.of(p1, p2));

        assertThrows(PedidosNoEntregadosException.class, () -> {
            cuentaService.cerrarCuenta(10L, MedioPago.EFECTIVO, 100000.0);
        });
    }

    @Test
    void debeLanzarExcepcionSiMontoInsuficiente() {
        cuentaService.abrirCuenta(10L);

        Pedido p1 = new Pedido();
        p1.setEstado(EstadoPedido.ENTREGADO);
        p1.setTotal(50000.0);

        when(pedidoService.obtenerPedidosPorMesa(10L)).thenReturn(List.of(p1));

        assertThrows(MontoInsuficienteException.class, () -> {
            cuentaService.cerrarCuenta(10L, MedioPago.EFECTIVO, 40000.0); // Recibe menos de lo que cuesta
        });
    }
}
