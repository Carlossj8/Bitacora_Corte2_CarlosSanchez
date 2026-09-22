package com.labrasaviva.service;

import com.labrasaviva.model.domain.Reserva;
import java.util.List;

public interface ReservaService {
    Reserva crearReserva(Reserva reserva);
    List<Reserva> obtenerTodas();
}
