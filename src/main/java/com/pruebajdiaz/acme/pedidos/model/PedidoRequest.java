package com.pruebajdiaz.acme.pedidos.model;

import lombok.Data;

@Data
public class PedidoRequest {
    /*
     * Puntos importantes a aclarar (JDIAZ):
     * uso la anotación @Data para simplificar el hecho de definir getters, setters
     * y constructor por defecto
     * y de esta manera, me concentro en lógica propia de negocio que tiene que ver
     * con el contenido de la prueba
     */
    private EnviarPedido enviarPedido;

    @Data
    public static class EnviarPedido {
        private String numPedido;
        private String cantidadPedido;
        private String codigoEAN;
        private String nombreProducto;
        private String numDocumento;
        private String direccion;
    }
}