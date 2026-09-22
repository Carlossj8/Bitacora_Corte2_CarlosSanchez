package com.labrasaviva.mapper.out;

import com.labrasaviva.dto.response.RegistroVehiculoResponseDTO;
import com.labrasaviva.model.domain.RegistroVehiculo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RegistroVehiculoMapperOut {
    RegistroVehiculoResponseDTO toResponseDTO(RegistroVehiculo registro);
}
