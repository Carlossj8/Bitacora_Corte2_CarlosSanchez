package com.labrasaviva.mapper.in;

import com.labrasaviva.dto.request.ItemPedidoRequestDTO;
import com.labrasaviva.dto.request.PedidoRequestDTO;
import com.labrasaviva.model.domain.ItemPedido;
import com.labrasaviva.model.domain.Pedido;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PedidoMapperIn {
    Pedido toDomain(PedidoRequestDTO dto);
    ItemPedido itemToDomain(ItemPedidoRequestDTO dto);
}
