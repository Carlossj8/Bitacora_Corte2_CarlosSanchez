package com.labrasaviva.mapper.out;

import com.labrasaviva.dto.response.EventoResponseDTO;
import com.labrasaviva.model.domain.EventoRestaurante;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventoMapperOut {

    EventoResponseDTO toResponseDTO(EventoRestaurante domain);
}
