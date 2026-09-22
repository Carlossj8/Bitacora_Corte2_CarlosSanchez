package com.labrasaviva.service.impl;

import com.labrasaviva.exception.EstadoInvalidoException;
import com.labrasaviva.exception.PedidoNoEncontradoException;
import com.labrasaviva.exception.PedidoNoModificableException;
import com.labrasaviva.exception.TerminoCoccionRequeridoException;
import com.labrasaviva.model.domain.EstadoPedido;
import com.labrasaviva.model.domain.ItemPedido;
import com.labrasaviva.model.domain.Pedido;
import com.labrasaviva.model.domain.Plato;
import com.labrasaviva.service.PedidoService;
import com.labrasaviva.service.PlatoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PlatoService platoService;
    private final List<Pedido> pedidos = new ArrayList<>();
    private final AtomicLong pedidoCounter = new AtomicLong(1);
    private final AtomicLong itemCounter = new AtomicLong(1);

    @Override
    public Pedido crearPedido(Long idMesa) {
        Pedido pedido = new Pedido();
        pedido.setId(pedidoCounter.getAndIncrement());
        pedido.setIdMesa(idMesa);
        pedido.setEstado(EstadoPedido.RECIBIDO);
        pedidos.add(pedido);
        log.info("Pedido creado con ID {} para la mesa {}", pedido.getId(), idMesa);
        return pedido;
    }

    @Override
    public Pedido obtenerPorId(Long id) {
        return Optional.ofNullable(pedidos.stream()
                        .filter(p -> p.getId().equals(id))
                        .findFirst()
                        .orElse(null))
                .orElseThrow(() -> new PedidoNoEncontradoException("Pedido no encontrado con ID: " + id));
    }

    @Override
    public List<Pedido> obtenerPedidosPorMesa(Long idMesa) {
        return pedidos.stream()
                .filter(p -> p.getIdMesa().equals(idMesa))
                .toList();
    }

    @Override
    public List<Pedido> obtenerTodos() {
        return pedidos;
    }

    @Override
    public Pedido agregarItem(Long idPedido, ItemPedido item) {
        Pedido pedido = obtenerPorId(idPedido);

        // RN-01: Un pedido solo puede modificarse mientras esté en estado RECIBIDO
        if (pedido.getEstado() != EstadoPedido.RECIBIDO) {
            throw new PedidoNoModificableException("No se pueden agregar ítems. El pedido ya no está en estado RECIBIDO.");
        }

        // Obtener el plato de la carta. Esto lanzará PlatoNoEncontradoException si no existe.
        Plato plato = platoService.obtenerPorId(item.getIdPlato());

        // RN-02: Plato no disponible
        if (!plato.getDisponible()) {
            throw new EstadoInvalidoException("El plato " + plato.getNombre() + " no está disponible actualmente.");
        }

        // RN-P01: Todo corte debe registrar su término de cocción
        if ("Cortes".equalsIgnoreCase(plato.getCategoria()) && item.getTerminoCoccion() == null) {
            throw new TerminoCoccionRequeridoException("El plato " + plato.getNombre() + " es un corte y requiere término de cocción.");
        }

        // Congelar precio y calcular subtotal
        item.setId(itemCounter.getAndIncrement());
        item.setNombrePlato(plato.getNombre());
        item.setPrecioCongelado(plato.getPrecio());
        item.setSubtotal(plato.getPrecio() * item.getCantidad());

        pedido.getItems().add(item);
        
        // Recalcular total de la cuenta
        Double nuevoTotal = pedido.getItems().stream()
                .mapToDouble(ItemPedido::getSubtotal)
                .sum();
        pedido.setTotal(nuevoTotal);

        log.info("Ítem agregado al pedido {}: {} x{} (Subtotal: {})", idPedido, plato.getNombre(), item.getCantidad(), item.getSubtotal());

        return pedido;
    }

    @Override
    public Pedido cambiarEstado(Long idPedido, EstadoPedido nuevoEstado) {
        Pedido pedido = obtenerPorId(idPedido);
        EstadoPedido estadoActual = pedido.getEstado();

        // Validar secuencia de estados: RECIBIDO -> EN_PREPARACION -> LISTO -> ENTREGADO
        boolean esValido = false;
        if (estadoActual == EstadoPedido.RECIBIDO && nuevoEstado == EstadoPedido.EN_PREPARACION) {
            esValido = true;
        } else if (estadoActual == EstadoPedido.EN_PREPARACION && nuevoEstado == EstadoPedido.LISTO) {
            esValido = true;
        } else if (estadoActual == EstadoPedido.LISTO && nuevoEstado == EstadoPedido.ENTREGADO) {
            esValido = true;
        }

        if (!esValido) {
            throw new EstadoInvalidoException("No se puede cambiar el estado de " + estadoActual + " a " + nuevoEstado);
        }

        pedido.setEstado(nuevoEstado);
        log.info("Pedido {} cambió a estado {}", idPedido, nuevoEstado);
        return pedido;
    }
}
