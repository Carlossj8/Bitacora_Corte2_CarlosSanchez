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
import com.labrasaviva.model.entity.ItemPedidoEntity;
import com.labrasaviva.model.entity.PedidoEntity;
import com.labrasaviva.repository.PedidoRepository;
import com.labrasaviva.service.EventoAuditoriaService;
import com.labrasaviva.service.PedidoService;
import com.labrasaviva.service.PlatoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoEntityMapper pedidoMapper;
    private final ItemPedidoEntityMapper itemPedidoMapper;
    private final PlatoService platoService;
    private final EventoAuditoriaService eventoAuditoriaService;

    @Override
    @Transactional
    public Pedido crearPedido(Long idMesa) {
        PedidoEntity entity = PedidoEntity.builder()
                .idMesa(idMesa)
                .estado(EstadoPedido.RECIBIDO)
                .items(new ArrayList<>())
                .total(0.0)
                .build();

        PedidoEntity guardado = pedidoRepository.save(entity);
        log.info("Pedido guardado en BD con ID {} para la mesa {}", guardado.getId(), idMesa);

        eventoAuditoriaService.registrarEvento(
                "PEDIDO_CREADO",
                "Pedido",
                guardado.getId(),
                "Comanda abierta para mesa " + idMesa,
                "MESERO",
                Map.of("idMesa", idMesa, "estado", EstadoPedido.RECIBIDO.name())
        );

        return pedidoMapper.toDomain(guardado);
    }

    @Override
    public Pedido obtenerPorId(Long id) {
        return pedidoRepository.findById(id)
                .map(pedidoMapper::toDomain)
                .orElseThrow(() -> new PedidoNoEncontradoException("Pedido no encontrado con ID: " + id));
    }

    @Override
    public List<Pedido> obtenerPedidosPorMesa(Long idMesa) {
        return pedidoRepository.findByIdMesa(idMesa).stream()
                .map(pedidoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAll().stream()
                .map(pedidoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Pedido agregarItem(Long idPedido, ItemPedido item) {
        PedidoEntity pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new PedidoNoEncontradoException("Pedido no encontrado con ID: " + idPedido));

        // RN-01: Un pedido solo puede modificarse mientras esté en estado RECIBIDO
        if (pedido.getEstado() != EstadoPedido.RECIBIDO) {
            throw new PedidoNoModificableException("No se pueden agregar ítems. El pedido ya no está en estado RECIBIDO.");
        }

        // Obtener el plato de la carta (lanzará PlatoNoEncontradoException si no existe)
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
        item.setNombrePlato(plato.getNombre());
        item.setPrecioCongelado(plato.getPrecio());
        item.setSubtotal(plato.getPrecio() * item.getCantidad());

        ItemPedidoEntity itemEntity = itemPedidoMapper.toEntity(item);
        itemEntity.setPedido(pedido);
        pedido.getItems().add(itemEntity);

        // Recalcular total de la comanda
        Double nuevoTotal = pedido.getItems().stream()
                .mapToDouble(ItemPedidoEntity::getSubtotal)
                .sum();
        pedido.setTotal(nuevoTotal);

        PedidoEntity guardado = pedidoRepository.save(pedido);
        log.info("Ítem agregado en BD al pedido {}: {} x{}", idPedido, plato.getNombre(), item.getCantidad());

        return pedidoMapper.toDomain(guardado);
    }

    @Override
    @Transactional
    public Pedido cambiarEstado(Long idPedido, EstadoPedido nuevoEstado) {
        PedidoEntity pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new PedidoNoEncontradoException("Pedido no encontrado con ID: " + idPedido));

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
        PedidoEntity guardado = pedidoRepository.save(pedido);
        log.info("Pedido {} cambió a estado {} en BD", idPedido, nuevoEstado);

        eventoAuditoriaService.registrarEvento(
                "ESTADO_PEDIDO_CAMBIADO",
                "Pedido",
                idPedido,
                "Estado del pedido actualizado a " + nuevoEstado,
                "COCINA",
                Map.of("estadoAnterior", estadoActual.name(), "nuevoEstado", nuevoEstado.name())
        );

        return pedidoMapper.toDomain(guardado);
    }
}
