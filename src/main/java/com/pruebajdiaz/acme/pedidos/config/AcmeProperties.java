package com.pruebajdiaz.acme.pedidos.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades de configuracion para el servicio ACME
 * Esta clase permite que Spring Boot y VSCode reconozcan las propiedades
 * definidas en application.properties correctamente
 */
@ConfigurationProperties(prefix = "acme.soap")
public class AcmeProperties {

    /**
     * URL completa del endpoint del servicio SOAP de ACME
     */
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}