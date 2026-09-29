package com.labrasaviva.mapper.persistence;

import com.labrasaviva.model.domain.Pedido;
import com.labrasaviva.model.entity.PedidoEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {ItemPedidoEntityMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PedidoEntityMapper {

    PedidoEntity toEntity(Pedido domain);

    Pedido toDomain(PedidoEntity entity);

    @AfterMapping
    default void linkItems(@MappingTarget PedidoEntity entity) {
        if (entity.getItems() != null) {
            entity.getItems().forEach(item -> item.setPedido(entity));
        }
    }
}
