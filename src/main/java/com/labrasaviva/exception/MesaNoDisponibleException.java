package com.labrasaviva.exception;

public class MesaNoDisponibleException extends RuntimeException {
    public MesaNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
