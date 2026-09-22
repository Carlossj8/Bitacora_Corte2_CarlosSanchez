package com.labrasaviva.service;

import com.labrasaviva.model.domain.RegistroVehiculo;
import java.util.List;

public interface RegistroVehiculoService {
    RegistroVehiculo registrarEntrada(String placa);
    RegistroVehiculo registrarSalida(String placa);
    List<RegistroVehiculo> obtenerTodos();
}
