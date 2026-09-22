package com.labrasaviva.service;

import com.labrasaviva.model.domain.Cuenta;
import com.labrasaviva.model.domain.MedioPago;

public interface CuentaService {
    Cuenta abrirCuenta(Long idMesa);
    Cuenta obtenerCuentaActiva(Long idMesa);
    Cuenta cerrarCuenta(Long idMesa, MedioPago medioPago, Double montoRecibido);
}
