package com.labrasaviva.mapper.out;

import com.labrasaviva.dto.response.PlatoResponseDTO;
import com.labrasaviva.model.domain.Plato;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlatoMapperOut {
    PlatoResponseDTO toResponseDTO(Plato plato);
}
