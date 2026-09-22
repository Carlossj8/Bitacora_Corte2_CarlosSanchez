package com.labrasaviva.service.impl;

import com.labrasaviva.exception.CuentaNoEncontradaException;
import com.labrasaviva.exception.MesaOcupadaException;
import com.labrasaviva.exception.MontoInsuficienteException;
import com.labrasaviva.exception.PedidosNoEntregadosException;
import com.labrasaviva.model.domain.Cuenta;
import com.labrasaviva.model.domain.EstadoCuenta;
import com.labrasaviva.model.domain.EstadoPedido;
import com.labrasaviva.model.domain.MedioPago;
import com.labrasaviva.model.domain.Pedido;
import com.labrasaviva.service.CuentaService;
import com.labrasaviva.service.PedidoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements CuentaService {

    private final PedidoService pedidoService;
    private final List<Cuenta> cuentas = new ArrayList<>();
    private final AtomicLong cuentaCounter = new AtomicLong(1);

    @Override
    public Cuenta abrirCuenta(Long idMesa) {
        // RN-03: Una mesa solo puede tener una cuenta abierta a la vez
        Optional<Cuenta> cuentaActiva = buscarCuentaActiva(idMesa);
        if (cuentaActiva.isPresent()) {
            throw new MesaOcupadaException("La mesa " + idMesa + " ya tiene una cuenta ABIERTA.");
        }

        Cuenta nuevaCuenta = new Cuenta();
        nuevaCuenta.setId(cuentaCounter.getAndIncrement());
        nuevaCuenta.setIdMesa(idMesa);
        nuevaCuenta.setEstado(EstadoCuenta.ABIERTA);
        nuevaCuenta.setFechaApertura(LocalDateTime.now());
        nuevaCuenta.setTotal(0.0);
        
        cuentas.add(nuevaCuenta);
        log.info("Cuenta {} abierta para la mesa {}", nuevaCuenta.getId(), idMesa);
        return nuevaCuenta;
    }

    @Override
    public Cuenta obtenerCuentaActiva(Long idMesa) {
        return buscarCuentaActiva(idMesa)
                .orElseThrow(() -> new CuentaNoEncontradaException("No hay una cuenta activa para la mesa " + idMesa));
    }

    @Override
    public Cuenta cerrarCuenta(Long idMesa, MedioPago medioPago, Double montoRecibido) {
        Cuenta cuenta = obtenerCuentaActiva(idMesa);
        
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
        
        log.info("Cuenta {} cerrada con éxito. Total pagado: {} mediante {}", cuenta.getId(), totalCalculado, medioPago);
        
        return cuenta;
    }

    private Optional<Cuenta> buscarCuentaActiva(Long idMesa) {
        return cuentas.stream()
                .filter(c -> c.getIdMesa().equals(idMesa) && c.getEstado() == EstadoCuenta.ABIERTA)
                .findFirst();
    }
}
