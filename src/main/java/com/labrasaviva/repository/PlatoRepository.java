package com.labrasaviva.repository;

import com.labrasaviva.model.entity.PlatoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlatoRepository extends JpaRepository<PlatoEntity, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    Optional<PlatoEntity> findByNombreIgnoreCase(String nombre);

    List<PlatoEntity> findByDisponibleTrue();

    List<PlatoEntity> findByCategoriaIgnoreCase(String categoria);
}