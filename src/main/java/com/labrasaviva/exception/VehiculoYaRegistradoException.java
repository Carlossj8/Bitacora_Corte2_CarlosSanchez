package com.labrasaviva.exception;

public class VehiculoYaRegistradoException extends RuntimeException {
    public VehiculoYaRegistradoException(String mensaje) {
        super(mensaje);
    }
}
