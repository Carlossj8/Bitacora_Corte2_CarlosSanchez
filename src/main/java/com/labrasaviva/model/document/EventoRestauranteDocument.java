package com.labrasaviva.model.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "eventos_restaurante")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoRestauranteDocument {

    @Id
    private String id; // Mongo usa String por defecto (ObjectId en BSON)

    private String tipo;        // "PEDIDO_CREADO", "PLATO_AGOTADO", "ESTADO_CAMBIADO", etc.
    private String entidadTipo; // "Pedido", "Plato", "Mesa", "Cuenta", "Reserva", "Vehiculo"
    private Long entidadId;
    private String descripcion;
    private String usuario;
    private LocalDateTime timestamp;

    // Mongo permite campos opcionales
    private Map<String, Object> metadatos;
}
