package com.labrasaviva.repository;

import com.labrasaviva.model.entity.RegistroVehiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroVehiculoRepository extends JpaRepository<RegistroVehiculoEntity, Long> {

    Optional<RegistroVehiculoEntity> findByPlacaIgnoreCaseAndSalidaIsNull(String placa);

    boolean existsByPlacaIgnoreCaseAndSalidaIsNull(String placa);

    List<RegistroVehiculoEntity> findBySalidaIsNull();
}
