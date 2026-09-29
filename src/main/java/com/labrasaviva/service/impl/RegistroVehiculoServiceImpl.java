package com.labrasaviva.service.impl;

import com.labrasaviva.exception.VehiculoNoEncontradoException;
import com.labrasaviva.exception.VehiculoYaRegistradoException;
import com.labrasaviva.mapper.persistence.RegistroVehiculoEntityMapper;
import com.labrasaviva.model.domain.RegistroVehiculo;
import com.labrasaviva.model.entity.RegistroVehiculoEntity;
import com.labrasaviva.repository.RegistroVehiculoRepository;
import com.labrasaviva.service.EventoAuditoriaService;
import com.labrasaviva.service.RegistroVehiculoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistroVehiculoServiceImpl implements RegistroVehiculoService {

    private final RegistroVehiculoRepository vehiculoRepository;
    private final RegistroVehiculoEntityMapper vehiculoMapper;
    private final EventoAuditoriaService eventoAuditoriaService;

    @Override
    @Transactional
    public RegistroVehiculo registrarEntrada(String placa) {
        if (vehiculoRepository.existsByPlacaIgnoreCaseAndSalidaIsNull(placa)) {
            throw new VehiculoYaRegistradoException("El vehículo con placa " + placa + " ya está en el parqueadero.");
        }

        RegistroVehiculoEntity entity = RegistroVehiculoEntity.builder()
                .placa(placa.toUpperCase())
                .entrada(LocalDateTime.now())
                .build();

        RegistroVehiculoEntity guardado = vehiculoRepository.save(entity);
        log.info("Entrada registrada en BD para vehículo con placa {}", placa);

        eventoAuditoriaService.registrarEvento(
                "VEHICULO_INGRESADO",
                "Vehiculo",
                guardado.getId(),
                "Ingreso de vehículo con placa " + guardado.getPlaca(),
                "OPERADOR_PARQUEADERO",
                Map.of("placa", guardado.getPlaca(), "entrada", guardado.getEntrada().toString())
        );

        return vehiculoMapper.toDomain(guardado);
    }

    @Override
    @Transactional
    public RegistroVehiculo registrarSalida(String placa) {
        RegistroVehiculoEntity registroActivo = vehiculoRepository.findByPlacaIgnoreCaseAndSalidaIsNull(placa)
                .orElseThrow(() -> new VehiculoNoEncontradoException("No se encontró entrada activa para la placa " + placa));

        registroActivo.setSalida(LocalDateTime.now());

        // Tarifa: 5000 por hora o fracción
        long minutos = Duration.between(registroActivo.getEntrada(), registroActivo.getSalida()).toMinutes();
        long horas = (minutos / 60) + (minutos % 60 > 0 ? 1 : 0);
        if (horas == 0) horas = 1; // Mínimo 1 hora

        registroActivo.setCobro(horas * 5000.0);
        RegistroVehiculoEntity guardado = vehiculoRepository.save(registroActivo);

        log.info("Salida registrada en BD para {}. Cobro: {}", placa, guardado.getCobro());

        eventoAuditoriaService.registrarEvento(
                "VEHICULO_SALIDA",
                "Vehiculo",
                guardado.getId(),
                "Salida de vehículo con placa " + guardado.getPlaca() + ". Total cobro: " + guardado.getCobro(),
                "OPERADOR_PARQUEADERO",
                Map.of("placa", guardado.getPlaca(), "cobro", guardado.getCobro())
        );

        return vehiculoMapper.toDomain(guardado);
    }

    @Override
    public List<RegistroVehiculo> obtenerTodos() {
        return vehiculoRepository.findAll().stream()
                .map(vehiculoMapper::toDomain)
                .collect(Collectors.toList());
    }
}
