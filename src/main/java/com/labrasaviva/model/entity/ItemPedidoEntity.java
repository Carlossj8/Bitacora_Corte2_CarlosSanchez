package com.labrasaviva.model.entity;

import com.labrasaviva.model.domain.TerminoCoccion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "items_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private PedidoEntity pedido;

    @Column(nullable = false)
    private Long idPlato;

    @Column(nullable = false, length = 100)
    private String nombrePlato;

    @Column(nullable = false)
    private Double precioCongelado;

    @Column(nullable = false)
    private Integer cantidad;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private TerminoCoccion terminoCoccion;

    @Column(length = 255)
    private String observaciones;

    @Column(nullable = false)
    private Double subtotal;
}
