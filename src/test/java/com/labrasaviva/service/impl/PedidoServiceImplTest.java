package com.labrasaviva.service.impl;

import com.labrasaviva.exception.EstadoInvalidoException;
import com.labrasaviva.exception.PedidoNoEncontradoException;
import com.labrasaviva.exception.PedidoNoModificableException;
import com.labrasaviva.exception.TerminoCoccionRequeridoException;
import com.labrasaviva.mapper.persistence.ItemPedidoEntityMapper;
import com.labrasaviva.mapper.persistence.PedidoEntityMapper;
import com.labrasaviva.model.domain.EstadoPedido;
import com.labrasaviva.model.domain.ItemPedido;
import com.labrasaviva.model.domain.Pedido;
import com.labrasaviva.model.domain.Plato;
import com.labrasaviva.model.domain.TerminoCoccion;
import com.labrasaviva.model.entity.ItemPedidoEntity;
import com.labrasaviva.model.entity.PedidoEntity;
import com.labrasaviva.repository.PedidoRepository;
import com.labrasaviva.service.EventoAuditoriaService;
import com.labrasaviva.service.PlatoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoEntityMapper pedidoMapper;

    @Mock
    private ItemPedidoEntityMapper itemPedidoMapper;

    @Mock
    private PlatoService platoService;

    @Mock
    private EventoAuditoriaService eventoAuditoriaService;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    @Test
    void debeCrearPedidoInicial() {
        PedidoEntity entityGuardada = PedidoEntity.builder()
                .id(1L)
                .idMesa(5L)
                .estado(EstadoPedido.RECIBIDO)
                .items(new ArrayList<>())
                .total(0.0)
                .build();

        Pedido domain = new Pedido();
        domain.setId(1L);
        domain.setIdMesa(5L);
        domain.setEstado(EstadoPedido.RECIBIDO);

        when(pedidoRepository.save(any(PedidoEntity.class))).thenReturn(entityGuardada);
        when(pedidoMapper.toDomain(entityGuardada)).thenReturn(domain);

        Pedido pedido = pedidoService.crearPedido(5L);

        assertNotNull(pedido.getId());
        assertEquals(5L, pedido.getIdMesa());
        assertEquals(EstadoPedido.RECIBIDO, pedido.getEstado());
        verify(pedidoRepository).save(any());
        verify(eventoAuditoriaService).registrarEvento(eq("PEDIDO_CREADO"), eq("Pedido"), eq(1L), any(), any(), any());
    }

    @Test
    void debeAgregarItemExitosamente() {
        PedidoEntity pedidoEntity = PedidoEntity.builder()
                .id(1L)
                .idMesa(1L)
                .estado(EstadoPedido.RECIBIDO)
                .items(new ArrayList<>())
                .total(0.0)
                .build();

        Plato plato = new Plato();
        plato.setId(10L);
        plato.setNombre("Gaseosa");
        plato.setPrecio(5000.0);
        plato.setCategoria("Bebidas");
        plato.setDisponible(true);

        ItemPedido item = new ItemPedido();
        item.setIdPlato(10L);
        item.setCantidad(2);

        ItemPedidoEntity itemEntity = ItemPedidoEntity.builder()
                .idPlato(10L)
                .nombrePlato("Gaseosa")
                .precioCongelado(5000.0)
                .cantidad(2)
                .subtotal(10000.0)
                .build();

        Pedido pedidoDomain = new Pedido();
        pedidoDomain.setId(1L);
        pedidoDomain.setTotal(10000.0);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoEntity));
        when(platoService.obtenerPorId(10L)).thenReturn(plato);
        when(itemPedidoMapper.toEntity(any(ItemPedido.class))).thenReturn(itemEntity);
        when(pedidoRepository.save(any(PedidoEntity.class))).thenReturn(pedidoEntity);
        when(pedidoMapper.toDomain(any(PedidoEntity.class))).thenReturn(pedidoDomain);

        Pedido resultado = pedidoService.agregarItem(1L, item);

        assertNotNull(resultado);
        assertEquals(10000.0, resultado.getTotal());
    }

    @Test
    void debeLanzarExcepcionSiCorteSinTermino() {
        PedidoEntity pedido = PedidoEntity.builder()
                .id(1L)
                .estado(EstadoPedido.RECIBIDO)
                .build();

        Plato platoCorte = new Plato();
        platoCorte.setId(20L);
        platoCorte.setNombre("Baby Beef");
        platoCorte.setCategoria("Cortes");
        platoCorte.setDisponible(true);

        ItemPedido item = new ItemPedido();
        item.setIdPlato(20L);
        item.setCantidad(1);
        item.setTerminoCoccion(null); // Falta término

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(platoService.obtenerPorId(20L)).thenReturn(platoCorte);

        assertThrows(TerminoCoccionRequeridoException.class, () -> pedidoService.agregarItem(1L, item));
    }

    @Test
    void debeLanzarExcepcionSiPlatoNoDisponible() {
        PedidoEntity pedido = PedidoEntity.builder()
                .id(1L)
                .estado(EstadoPedido.RECIBIDO)
                .build();

        Plato platoAgotado = new Plato();
        platoAgotado.setId(30L);
        platoAgotado.setNombre("Costillas");
        platoAgotado.setDisponible(false); // No disponible

        ItemPedido item = new ItemPedido();
        item.setIdPlato(30L);
        item.setCantidad(1);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(platoService.obtenerPorId(30L)).thenReturn(platoAgotado);

        assertThrows(EstadoInvalidoException.class, () -> pedidoService.agregarItem(1L, item));
    }

    @Test
    void debeLanzarExcepcionSiPedidoYaNoEstaEnRecibido() {
        PedidoEntity pedido = PedidoEntity.builder()
                .id(1L)
                .estado(EstadoPedido.EN_PREPARACION) // Ya avanzó
                .build();

        ItemPedido item = new ItemPedido();
        item.setIdPlato(1L);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(PedidoNoModificableException.class, () -> pedidoService.agregarItem(1L, item));
    }

    @Test
    void debeCambiarEstadoCorrectamente() {
        PedidoEntity pedido = PedidoEntity.builder()
                .id(1L)
                .estado(EstadoPedido.RECIBIDO)
                .build();

        Pedido pedidoActualizado = new Pedido();
        pedidoActualizado.setId(1L);
        pedidoActualizado.setEstado(EstadoPedido.EN_PREPARACION);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(PedidoEntity.class))).thenReturn(pedido);
        when(pedidoMapper.toDomain(any(PedidoEntity.class))).thenReturn(pedidoActualizado);

        Pedido resultado = pedidoService.cambiarEstado(1L, EstadoPedido.EN_PREPARACION);

        assertEquals(EstadoPedido.EN_PREPARACION, resultado.getEstado());
        verify(eventoAuditoriaService).registrarEvento(eq("ESTADO_PEDIDO_CAMBIADO"), eq("Pedido"), eq(1L), any(), any(), any());
    }

    @Test
    void debeLanzarExcepcionSiTransicionInvalida() {
        PedidoEntity pedido = PedidoEntity.builder()
                .id(1L)
                .estado(EstadoPedido.RECIBIDO)
                .build();

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Intento directo a ENTREGADO sin pasar por preparación
        assertThrows(EstadoInvalidoException.class, () -> pedidoService.cambiarEstado(1L, EstadoPedido.ENTREGADO));
    }
}
