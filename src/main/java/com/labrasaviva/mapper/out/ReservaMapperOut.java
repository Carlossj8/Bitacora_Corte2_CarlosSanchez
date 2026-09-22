package com.labrasaviva.mapper.out;

import com.labrasaviva.dto.response.ReservaResponseDTO;
import com.labrasaviva.model.domain.Reserva;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservaMapperOut {
    ReservaResponseDTO toResponseDTO(Reserva reserva);
}
