package com.labrasaviva.mapper.persistence;

import com.labrasaviva.model.document.EventoRestauranteDocument;
import com.labrasaviva.model.domain.EventoRestaurante;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventoMapper {

    @Mapping(target = "id", ignore = true)
    EventoRestauranteDocument toDocument(EventoRestaurante evento);

    EventoRestaurante toDomain(EventoRestauranteDocument doc);
}
