package com.labrasaviva.service.impl;

import com.labrasaviva.exception.MesaNoDisponibleException;
import com.labrasaviva.mapper.persistence.ReservaEntityMapper;
import com.labrasaviva.model.domain.Reserva;
import com.labrasaviva.model.entity.ReservaEntity;
import com.labrasaviva.repository.ReservaRepository;
import com.labrasaviva.service.EventoAuditoriaService;
import com.labrasaviva.service.ReservaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final ReservaEntityMapper reservaMapper;
    private final EventoAuditoriaService eventoAuditoriaService;

    @Override
    @Transactional
    public Reserva crearReserva(Reserva reserva) {
        // Validar que la mesa no tenga otra reserva dentro de un margen menor a 2 horas
        List<ReservaEntity> reservasMesa = reservaRepository.findByIdMesa(reserva.getIdMesa());

        boolean mesaOcupada = reservasMesa.stream()
                .anyMatch(r -> Math.abs(Duration.between(r.getFechaHora(), reserva.getFechaHora()).toHours()) < 2);

        if (mesaOcupada) {
            throw new MesaNoDisponibleException("La mesa " + reserva.getIdMesa() + " ya está reservada para esa hora.");
        }

        ReservaEntity entity = reservaMapper.toEntity(reserva);
        ReservaEntity guardado = reservaRepository.save(entity);
        log.info("Reserva guardada en BD con éxito para {} en la mesa {}", guardado.getCliente(), guardado.getIdMesa());

        eventoAuditoriaService.registrarEvento(
                "RESERVA_CREADA",
                "Reserva",
                guardado.getId(),
                "Reserva creada para " + guardado.getCliente() + " en mesa " + guardado.getIdMesa(),
                "CLIENTE_O_MESERO",
                Map.of("idMesa", guardado.getIdMesa(), "fechaHora", guardado.getFechaHora().toString(), "comensales", guardado.getComensales())
        );

        return reservaMapper.toDomain(guardado);
    }

    @Override
    public List<Reserva> obtenerTodas() {
        return reservaRepository.findAll().stream()
                .map(reservaMapper::toDomain)
                .collect(Collectors.toList());
    }
}
