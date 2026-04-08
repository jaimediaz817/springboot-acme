package com.pruebajdiaz.acme.pedidos.controller;

import com.pruebajdiaz.acme.pedidos.model.PedidoRequest;
import com.pruebajdiaz.acme.pedidos.model.PedidoResponse;
import com.pruebajdiaz.acme.pedidos.service.PedidoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/pedidos")
public class PedidosController {

    private final PedidoService pedidoService;

    // Constructor injection (recommended by SonarQube instead of field injection)
    public PedidosController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> enviarPedido(
            @RequestBody PedidoRequest request) {
        return ResponseEntity.ok(pedidoService.procesarPedido(request));
    }

}