package com.labrasaviva.exception;

public class PedidoNoEncontradoException extends RuntimeException {
    public PedidoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
