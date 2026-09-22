package com.labrasaviva.service.impl;

import com.labrasaviva.exception.MesaNoDisponibleException;
import com.labrasaviva.model.domain.Reserva;
import com.labrasaviva.service.ReservaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class ReservaServiceImpl implements ReservaService {

    private final List<Reserva> reservas = new ArrayList<>();
    private final AtomicLong reservaCounter = new AtomicLong(1);

    @Override
    public Reserva crearReserva(Reserva reserva) {
        // Validar que la mesa no tenga otra reserva dentro de la misma hora
        boolean mesaOcupada = reservas.stream()
                .anyMatch(r -> r.getIdMesa().equals(reserva.getIdMesa()) &&
                        Math.abs(java.time.Duration.between(r.getFechaHora(), reserva.getFechaHora()).toHours()) < 2);

        if (mesaOcupada) {
            throw new MesaNoDisponibleException("La mesa " + reserva.getIdMesa() + " ya está reservada para esa hora.");
        }

        reserva.setId(reservaCounter.getAndIncrement());
        reservas.add(reserva);
        log.info("Reserva creada con éxito para {} en la mesa {}", reserva.getCliente(), reserva.getIdMesa());
        return reserva;
    }

    @Override
    public List<Reserva> obtenerTodas() {
        return reservas;
    }
}
