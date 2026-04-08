package com.pruebajdiaz.acme.pedidos.model;

import lombok.Data;

@Data
public class PedidoResponse {
    /*
     * Puntos importantes a aclarar (JDIAZ):
     * uso la anotación @Data para simplificar el hecho de definir getters, setters
     * y constructor por defecto
     * y de esta manera, me concentro en lógica propia de negocio que tiene que ver
     * con el contenido de la prueba
     */
    private EnviarPedidoRespuesta enviarPedidoRespuesta;

    @Data
    public static class EnviarPedidoRespuesta {
        private String codigoEnvio;
        private String estado;
    }
}