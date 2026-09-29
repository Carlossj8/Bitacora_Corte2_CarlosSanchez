package com.labrasaviva.mapper.persistence;

import com.labrasaviva.model.domain.ItemPedido;
import com.labrasaviva.model.entity.ItemPedidoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemPedidoEntityMapper {

    @Mapping(target = "pedido", ignore = true)
    ItemPedidoEntity toEntity(ItemPedido domain);

    ItemPedido toDomain(ItemPedidoEntity entity);
}
