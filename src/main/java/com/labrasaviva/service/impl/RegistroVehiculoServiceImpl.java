package com.labrasaviva.service.impl;

import com.labrasaviva.exception.VehiculoNoEncontradoException;
import com.labrasaviva.exception.VehiculoYaRegistradoException;
import com.labrasaviva.model.domain.RegistroVehiculo;
import com.labrasaviva.service.RegistroVehiculoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class RegistroVehiculoServiceImpl implements RegistroVehiculoService {

    private final List<RegistroVehiculo> registros = new ArrayList<>();
    private final AtomicLong registroCounter = new AtomicLong(1);

    @Override
    public RegistroVehiculo registrarEntrada(String placa) {
        boolean yaEnParqueadero = registros.stream()
                .anyMatch(r -> r.getPlaca().equalsIgnoreCase(placa) && r.getSalida() == null);

        if (yaEnParqueadero) {
            throw new VehiculoYaRegistradoException("El vehículo con placa " + placa + " ya está en el parqueadero.");
        }

        RegistroVehiculo registro = new RegistroVehiculo();
        registro.setId(registroCounter.getAndIncrement());
        registro.setPlaca(placa);
        registro.setEntrada(LocalDateTime.now());
        
        registros.add(registro);
        log.info("Entrada registrada para el vehículo con placa {}", placa);
        return registro;
    }

    @Override
    public RegistroVehiculo registrarSalida(String placa) {
        RegistroVehiculo registroActivo = registros.stream()
                .filter(r -> r.getPlaca().equalsIgnoreCase(placa) && r.getSalida() == null)
                .findFirst()
                .orElseThrow(() -> new VehiculoNoEncontradoException("No se encontró entrada activa para la placa " + placa));

        registroActivo.setSalida(LocalDateTime.now());
        
        // Calcular cobro simulado: 5000 por hora o fracción
        long minutos = java.time.Duration.between(registroActivo.getEntrada(), registroActivo.getSalida()).toMinutes();
        long horas = (minutos / 60) + (minutos % 60 > 0 ? 1 : 0);
        if (horas == 0) horas = 1; // Mínimo 1 hora
        
        registroActivo.setCobro(horas * 5000.0);
        
        log.info("Salida registrada para {}. Cobro: {}", placa, registroActivo.getCobro());
        return registroActivo;
    }

    @Override
    public List<RegistroVehiculo> obtenerTodos() {
        return registros;
    }
}
