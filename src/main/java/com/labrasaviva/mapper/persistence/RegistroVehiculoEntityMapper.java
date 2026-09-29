package com.labrasaviva.mapper.persistence;

import com.labrasaviva.model.domain.RegistroVehiculo;
import com.labrasaviva.model.entity.RegistroVehiculoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RegistroVehiculoEntityMapper {

    RegistroVehiculoEntity toEntity(RegistroVehiculo domain);

    RegistroVehiculo toDomain(RegistroVehiculoEntity entity);
}
