package com.labrasaviva.exception;

public class VehiculoNoEncontradoException extends RuntimeException {
    public VehiculoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
