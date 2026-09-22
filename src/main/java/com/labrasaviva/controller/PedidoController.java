package com.labrasaviva.controller;

import com.labrasaviva.dto.request.ItemPedidoRequestDTO;
import com.labrasaviva.dto.request.PedidoRequestDTO;
import com.labrasaviva.dto.response.PedidoResponseDTO;
import com.labrasaviva.mapper.in.PedidoMapperIn;
import com.labrasaviva.mapper.out.PedidoMapperOut;
import com.labrasaviva.model.domain.EstadoPedido;
import com.labrasaviva.model.domain.ItemPedido;
import com.labrasaviva.model.domain.Pedido;
import com.labrasaviva.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Operaciones sobre los pedidos del restaurante")
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoMapperIn mapperIn;
    private final PedidoMapperOut mapperOut;

    @Operation(summary = "Crear un nuevo pedido para una mesa")
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> crearPedido(@Valid @RequestBody PedidoRequestDTO request) {
        log.info("Petición para crear pedido en la mesa {}", request.getIdMesa());
        Pedido pedido = pedidoService.crearPedido(request.getIdMesa());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapperOut.toResponseDTO(pedido));
    }

    @Operation(summary = "Obtener un pedido por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.info("Petición para obtener el pedido {}", id);
        Pedido pedido = pedidoService.obtenerPorId(id);
        return ResponseEntity.ok(mapperOut.toResponseDTO(pedido));
    }

    @Operation(summary = "Obtener todos los pedidos")
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> obtenerTodos() {
        log.info("Petición para obtener todos los pedidos");
        List<Pedido> pedidos = pedidoService.obtenerTodos();
        List<PedidoResponseDTO> response = pedidos.stream()
                .map(mapperOut::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Agregar un ítem a un pedido existente (RF-03)")
    @PostMapping("/{id}/items")
    public ResponseEntity<PedidoResponseDTO> agregarItem(@PathVariable Long id, @Valid @RequestBody ItemPedidoRequestDTO itemRequest) {
        log.info("Petición para agregar plato {} al pedido {}", itemRequest.getIdPlato(), id);
        ItemPedido item = mapperIn.itemToDomain(itemRequest);
        Pedido pedidoActualizado = pedidoService.agregarItem(id, item);
        return ResponseEntity.ok(mapperOut.toResponseDTO(pedidoActualizado));
    }

    @Operation(summary = "Cambiar el estado de un pedido (RF-06)")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponseDTO> cambiarEstado(@PathVariable Long id, @RequestParam EstadoPedido nuevoEstado) {
        log.info("Petición para cambiar el estado del pedido {} a {}", id, nuevoEstado);
        Pedido pedidoActualizado = pedidoService.cambiarEstado(id, nuevoEstado);
        return ResponseEntity.ok(mapperOut.toResponseDTO(pedidoActualizado));
    }
}
