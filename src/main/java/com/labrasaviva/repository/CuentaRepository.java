package com.labrasaviva.repository;

import com.labrasaviva.model.domain.EstadoCuenta;
import com.labrasaviva.model.entity.CuentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<CuentaEntity, Long> {

    Optional<CuentaEntity> findByIdMesaAndEstado(Long idMesa, EstadoCuenta estado);

    boolean existsByIdMesaAndEstado(Long idMesa, EstadoCuenta estado);
}
