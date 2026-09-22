package com.labrasaviva.mapper.in;

import com.labrasaviva.dto.request.PlatoRequestDTO;
import com.labrasaviva.model.domain.Plato;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PlatoMapperIn {
    @Mapping(target = "id", ignore = true)
    Plato toDomain(PlatoRequestDTO requestDTO);
}
