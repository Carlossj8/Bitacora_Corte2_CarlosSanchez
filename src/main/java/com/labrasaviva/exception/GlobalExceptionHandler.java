package com.labrasaviva.exception;

import com.labrasaviva.dto.response.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PlatoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> handlePlatoNoEncontrado(PlatoNoEncontradoException ex, HttpServletRequest request) {
        ErrorResponseDTO error = ErrorResponseDTO.of(
                HttpStatus.NOT_FOUND.value(),
                "No Encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(PlatoYaExisteException.class)
    public ResponseEntity<ErrorResponseDTO> handlePlatoYaExiste(PlatoYaExisteException ex, HttpServletRequest request) {
        ErrorResponseDTO error = ErrorResponseDTO.of(
                HttpStatus.CONFLICT.value(),
                "Conflicto",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler({
            PedidoNoEncontradoException.class, 
            CuentaNoEncontradaException.class,
            VehiculoNoEncontradoException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleNoEncontrado(RuntimeException ex, HttpServletRequest request) {
        ErrorResponseDTO error = ErrorResponseDTO.of(
                HttpStatus.NOT_FOUND.value(),
                "No Encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler({
            EstadoInvalidoException.class,
            PedidoNoModificableException.class,
            TerminoCoccionRequeridoException.class,
            MesaOcupadaException.class,
            PedidosNoEntregadosException.class,
            MontoInsuficienteException.class,
            MesaNoDisponibleException.class,
            VehiculoYaRegistradoException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleReglasDeNegocio(RuntimeException ex, HttpServletRequest request) {
        ErrorResponseDTO error = ErrorResponseDTO.of(
                HttpStatus.CONFLICT.value(),
                "Regla de Negocio Incumplida",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errores = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ErrorResponseDTO error = ErrorResponseDTO.of(
                HttpStatus.BAD_REQUEST.value(),
                "Petición Inválida",
                "Errores de validación: " + errores,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneralException(Exception ex, HttpServletRequest request) {
        ErrorResponseDTO error = ErrorResponseDTO.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error Interno del Servidor",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
