package com.labrasaviva.repository;

import com.labrasaviva.model.domain.EstadoPedido;
import com.labrasaviva.model.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {

    List<PedidoEntity> findByIdMesa(Long idMesa);

    List<PedidoEntity> findByIdMesaAndEstado(Long idMesa, EstadoPedido estado);

    List<PedidoEntity> findByEstado(EstadoPedido estado);
}
