package com.pruebajdiaz.acme.pedidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.pruebajdiaz.acme.pedidos.config.AcmeProperties;

@SpringBootApplication
@EnableConfigurationProperties(AcmeProperties.class)
public class PedidosApplication {

	public static void main(String[] args) {
		SpringApplication.run(PedidosApplication.class, args);
	}

}
