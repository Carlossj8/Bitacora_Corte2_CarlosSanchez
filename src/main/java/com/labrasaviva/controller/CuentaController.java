package com.labrasaviva.controller;

import com.labrasaviva.dto.request.CuentaCierreRequestDTO;
import com.labrasaviva.dto.response.CuentaResponseDTO;
import com.labrasaviva.mapper.out.CuentaMapperOut;
import com.labrasaviva.model.domain.Cuenta;
import com.labrasaviva.service.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
@Tag(name = "Cuentas", description = "Operaciones sobre las cuentas de las mesas")
public class CuentaController {

    private final CuentaService cuentaService;
    private final CuentaMapperOut mapperOut;

    @Operation(summary = "Abrir una cuenta para una mesa (RF-09 inicio)")
    @PostMapping("/mesa/{idMesa}/abrir")
    public ResponseEntity<CuentaResponseDTO> abrirCuenta(@PathVariable Long idMesa) {
        log.info("Petición para abrir cuenta en la mesa {}", idMesa);
        Cuenta cuenta = cuentaService.abrirCuenta(idMesa);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapperOut.toResponseDTO(cuenta));
    }

    @Operation(summary = "Obtener cuenta activa de una mesa")
    @GetMapping("/mesa/{idMesa}")
    public ResponseEntity<CuentaResponseDTO> obtenerCuentaActiva(@PathVariable Long idMesa) {
        log.info("Petición para obtener cuenta activa de la mesa {}", idMesa);
        Cuenta cuenta = cuentaService.obtenerCuentaActiva(idMesa);
        return ResponseEntity.ok(mapperOut.toResponseDTO(cuenta));
    }

    @Operation(summary = "Cerrar la cuenta de una mesa y registrar el pago (RF-09 cierre)")
    @PostMapping("/mesa/{idMesa}/cerrar")
    public ResponseEntity<CuentaResponseDTO> cerrarCuenta(@PathVariable Long idMesa, @Valid @RequestBody CuentaCierreRequestDTO request) {
        log.info("Petición para cerrar cuenta de la mesa {} pagando con {}", idMesa, request.getMedioPago());
        Cuenta cuenta = cuentaService.cerrarCuenta(idMesa, request.getMedioPago(), request.getMontoRecibido());
        
        CuentaResponseDTO response = mapperOut.toResponseDTO(cuenta);
        // Simulamos la generación de comprobante y factura electrónica
        response.setComprobantePago(UUID.randomUUID().toString());
        response.setNumeroFactura("FE-" + System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}
