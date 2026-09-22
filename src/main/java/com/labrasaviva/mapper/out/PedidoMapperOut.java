package com.labrasaviva.mapper.out;

import com.labrasaviva.dto.response.ItemPedidoResponseDTO;
import com.labrasaviva.dto.response.PedidoResponseDTO;
import com.labrasaviva.model.domain.ItemPedido;
import com.labrasaviva.model.domain.Pedido;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PedidoMapperOut {
    PedidoResponseDTO toResponseDTO(Pedido pedido);
    ItemPedidoResponseDTO itemToResponseDTO(ItemPedido item);
}
