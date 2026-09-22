package com.labrasaviva.mapper.out;

import com.labrasaviva.dto.response.CuentaResponseDTO;
import com.labrasaviva.model.domain.Cuenta;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CuentaMapperOut {
    CuentaResponseDTO toResponseDTO(Cuenta cuenta);
}
