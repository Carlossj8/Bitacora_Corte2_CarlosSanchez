package com.labrasaviva.service;

import com.labrasaviva.model.domain.EstadoPedido;
import com.labrasaviva.model.domain.ItemPedido;
import com.labrasaviva.model.domain.Pedido;

import java.util.List;

public interface PedidoService {
    Pedido crearPedido(Long idMesa);
    Pedido obtenerPorId(Long id);
    List<Pedido> obtenerPedidosPorMesa(Long idMesa);
    List<Pedido> obtenerTodos();
    Pedido agregarItem(Long idPedido, ItemPedido item);
    Pedido cambiarEstado(Long idPedido, EstadoPedido nuevoEstado);
}
