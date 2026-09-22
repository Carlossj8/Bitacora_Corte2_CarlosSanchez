package com.labrasaviva.service.impl;

import com.labrasaviva.exception.EstadoInvalidoException;
import com.labrasaviva.exception.PedidoNoEncontradoException;
import com.labrasaviva.exception.PedidoNoModificableException;
import com.labrasaviva.exception.TerminoCoccionRequeridoException;
import com.labrasaviva.model.domain.EstadoPedido;
import com.labrasaviva.model.domain.ItemPedido;
import com.labrasaviva.model.domain.Pedido;
import com.labrasaviva.model.domain.Plato;
import com.labrasaviva.service.PlatoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock
    private PlatoService platoService;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void debeCrearPedidoInicial() {
        Pedido pedido = pedidoService.crearPedido(5L);
        assertNotNull(pedido.getId());
        assertEquals(5L, pedido.getIdMesa());
        assertEquals(EstadoPedido.RECIBIDO, pedido.getEstado());
        assertTrue(pedido.getItems().isEmpty());
    }

    @Test
    void debeAgregarItemExitosamente() {
        Pedido pedido = pedidoService.crearPedido(1L);
        
        Plato platoMock = new Plato();
        platoMock.setNombre("Sopa");
        platoMock.setCategoria("Sopas");
        platoMock.setPrecio(15000.0);
        platoMock.setDisponible(true);
        when(platoService.obtenerPorId(anyLong())).thenReturn(platoMock);

        ItemPedido item = new ItemPedido();
        item.setIdPlato(10L);
        item.setCantidad(2);

        Pedido resultado = pedidoService.agregarItem(pedido.getId(), item);

        assertEquals(1, resultado.getItems().size());
        assertEquals(30000.0, resultado.getTotal());
        assertEquals("Sopa", resultado.getItems().get(0).getNombrePlato());
        assertEquals(15000.0, resultado.getItems().get(0).getPrecioCongelado());
    }

    @Test
    void debeLanzarExcepcionAlAgregarCorteSinTermino() {
        Pedido pedido = pedidoService.crearPedido(1L);
        
        Plato platoMock = new Plato();
        platoMock.setNombre("Punta de Anca");
        platoMock.setCategoria("Cortes"); // Categoría sensible
        platoMock.setDisponible(true);
        when(platoService.obtenerPorId(anyLong())).thenReturn(platoMock);

        ItemPedido item = new ItemPedido();
        item.setIdPlato(10L);
        item.setCantidad(1);
        // Sin término de cocción

        assertThrows(TerminoCoccionRequeridoException.class, () -> {
            pedidoService.agregarItem(pedido.getId(), item);
        });
    }

    @Test
    void debeLanzarExcepcionAlAgregarItemAPedidoEnPreparacion() {
        Pedido pedido = pedidoService.crearPedido(1L);
        pedidoService.cambiarEstado(pedido.getId(), EstadoPedido.EN_PREPARACION);

        ItemPedido item = new ItemPedido();
        
        assertThrows(PedidoNoModificableException.class, () -> {
            pedidoService.agregarItem(pedido.getId(), item);
        });
    }

    @Test
    void debePermitirCambioDeEstadoValido() {
        Pedido pedido = pedidoService.crearPedido(1L); // Estado: RECIBIDO
        
        Pedido actualizado = pedidoService.cambiarEstado(pedido.getId(), EstadoPedido.EN_PREPARACION);
        assertEquals(EstadoPedido.EN_PREPARACION, actualizado.getEstado());
        
        actualizado = pedidoService.cambiarEstado(pedido.getId(), EstadoPedido.LISTO);
        assertEquals(EstadoPedido.LISTO, actualizado.getEstado());
        
        actualizado = pedidoService.cambiarEstado(pedido.getId(), EstadoPedido.ENTREGADO);
        assertEquals(EstadoPedido.ENTREGADO, actualizado.getEstado());
    }

    @Test
    void debeLanzarExcepcionPorSaltoDeEstado() {
        Pedido pedido = pedidoService.crearPedido(1L); // Estado: RECIBIDO
        
        assertThrows(EstadoInvalidoException.class, () -> {
            pedidoService.cambiarEstado(pedido.getId(), EstadoPedido.LISTO); // Salto inválido
        });
    }
}
