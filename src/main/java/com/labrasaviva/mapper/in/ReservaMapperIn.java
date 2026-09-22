package com.labrasaviva.mapper.in;

import com.labrasaviva.dto.request.ReservaRequestDTO;
import com.labrasaviva.model.domain.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservaMapperIn {
    @Mapping(target = "id", ignore = true)
    Reserva toDomain(ReservaRequestDTO dto);
}
