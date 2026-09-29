package com.labrasaviva.mapper.persistence;

import com.labrasaviva.model.domain.Plato;
import com.labrasaviva.model.entity.PlatoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PlatoEntityMapper {

    PlatoEntity toEntity(Plato plato);

    Plato toDomain(PlatoEntity entity);
}
