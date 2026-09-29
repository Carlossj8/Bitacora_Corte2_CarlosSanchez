package com.labrasaviva.mapper.persistence;

import com.labrasaviva.model.domain.Reserva;
import com.labrasaviva.model.entity.ReservaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReservaEntityMapper {

    ReservaEntity toEntity(Reserva domain);

    Reserva toDomain(ReservaEntity entity);
}
