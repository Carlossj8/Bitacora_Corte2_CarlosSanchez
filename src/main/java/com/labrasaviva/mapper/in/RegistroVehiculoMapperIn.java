package com.labrasaviva.mapper.in;

import com.labrasaviva.dto.request.RegistroVehiculoRequestDTO;
import com.labrasaviva.model.domain.RegistroVehiculo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegistroVehiculoMapperIn {
    @Mapping(target = "id", ignore = true)
    RegistroVehiculo toDomain(RegistroVehiculoRequestDTO dto);
}
