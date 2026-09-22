package com.labrasaviva.exception;

public class PedidosNoEntregadosException extends RuntimeException {
    public PedidosNoEntregadosException(String mensaje) {
        super(mensaje);
    }
}
