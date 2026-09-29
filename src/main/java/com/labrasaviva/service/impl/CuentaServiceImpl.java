package com.labrasaviva.service.impl;

import com.labrasaviva.exception.CuentaNoEncontradaException;
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
import com.labrasaviva.service.CuentaService;
import com.labrasaviva.service.EventoAuditoriaService;
import com.labrasaviva.service.PedidoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final CuentaEntityMapper cuentaMapper;
    private final PedidoService pedidoService;
    private final EventoAuditoriaService eventoAuditoriaService;

    @Override
    @Transactional
    public Cuenta abrirCuenta(Long idMesa) {
        // RN-03: Una mesa solo puede tener una cuenta abierta a la vez
        if (cuentaRepository.existsByIdMesaAndEstado(idMesa, EstadoCuenta.ABIERTA)) {
            throw new MesaOcupadaException("La mesa " + idMesa + " ya tiene una cuenta ABIERTA.");
        }

        CuentaEntity entity = CuentaEntity.builder()
                .idMesa(idMesa)
                .estado(EstadoCuenta.ABIERTA)
                .fechaApertura(LocalDateTime.now())
                .total(0.0)
                .build();

        CuentaEntity guardado = cuentaRepository.save(entity);
        log.info("Cuenta {} abierta para la mesa {} guardada en BD", guardado.getId(), idMesa);

        eventoAuditoriaService.registrarEvento(
                "CUENTA_ABIERTA",
                "Cuenta",
                guardado.getId(),
                "Cuenta abierta para la mesa " + idMesa,
                "MESERO",
                Map.of("idMesa", idMesa)
        );

        return cuentaMapper.toDomain(guardado);
    }

    @Override
    public Cuenta obtenerCuentaActiva(Long idMesa) {
        return cuentaRepository.findByIdMesaAndEstado(idMesa, EstadoCuenta.ABIERTA)
                .map(cuentaMapper::toDomain)
                .orElseThrow(() -> new CuentaNoEncontradaException("No hay una cuenta activa para la mesa " + idMesa));
    }

    @Override
    @Transactional
    public Cuenta cerrarCuenta(Long idMesa, MedioPago medioPago, Double montoRecibido) {
        CuentaEntity cuenta = cuentaRepository.findByIdMesaAndEstado(idMesa, EstadoCuenta.ABIERTA)
                .orElseThrow(() -> new CuentaNoEncontradaException("No hay una cuenta activa para la mesa " + idMesa));

        List<Pedido> pedidos = pedidoService.obtenerPedidosPorMesa(idMesa);

        // RN-09: Validar que todos los pedidos estén ENTREGADOS
        boolean hayPedidosPendientes = pedidos.stream()
                .anyMatch(p -> p.getEstado() != EstadoPedido.ENTREGADO);

        if (hayPedidosPendientes) {
            throw new PedidosNoEntregadosException("No se puede cerrar la cuenta. Hay pedidos que no han sido entregados.");
        }

        // RN-04: El total se calcula con los precios congelados
        Double totalCalculado = pedidos.stream()
                .mapToDouble(Pedido::getTotal)
                .sum();
        cuenta.setTotal(totalCalculado);

        // Validar monto
        if (montoRecibido < totalCalculado) {
            throw new MontoInsuficienteException("El monto recibido (" + montoRecibido + ") es menor al total de la cuenta (" + totalCalculado + ").");
        }

        cuenta.setEstado(EstadoCuenta.CERRADA);
        CuentaEntity guardado = cuentaRepository.save(cuenta);

        log.info("Cuenta {} cerrada con éxito en BD. Total pagado: {} mediante {}", guardado.getId(), totalCalculado, medioPago);

        eventoAuditoriaService.registrarEvento(
                "CUENTA_CERRADA",
                "Cuenta",
                guardado.getId(),
                "Cuenta cerrada y pagada para mesa " + idMesa,
                "CAJERO",
                Map.of("total", totalCalculado, "medioPago", medioPago != null ? medioPago.name() : "NO_ESPECIFICADO", "montoRecibido", montoRecibido)
        );

        return cuentaMapper.toDomain(guardado);
    }
}
