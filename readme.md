# ACME Pedidos - Servicio SOAP

Servicio de integración con API SOAP de ACME para gestión de pedidos.
Desarrollado como prueba técnica con Spring Boot.

---

##  Ejecución en entorno local:  Ejecutar con Docker (ESTO LO REQUIERE LA PRUEBA)

 Pre-requisitos:
- Docker Desktop instalado y ejecutandose

```bash
# 1. Generar JAR compilado
mvn clean package -DskipTests

# 2. Construir imagen Docker
docker build -t acme-pedidos .

# 3. Ejecutar contenedor
docker run -p 8080:8080 acme-pedidos
```

 O usando docker-compose:
```bash
mvn clean package -DskipTests && docker-compose up --build
```

---

##  Verificar funcionamiento

Una vez levantada la aplicación puedes probar el endpoint:

```
GET http://localhost:8080/api/pedidos/consultar/{idPedido}
```

Ejemplo de prueba:
```
http://localhost:8080/api/pedidos/consultar/12345
```

---

##  Configuración

La URL del servicio SOAP de ACME se configura en `application.properties`:

| Entorno          | URL configurada                                                            |
| ---------------- | -------------------------------------------------------------------------- |
| Producción       | `https://jaimediaz.dev/v3/19217075-6d4e-4818-98bc-416d1feb7b84`            |
| Local sin Docker | `http://localhost:8000/v3/19217075-6d4e-4818-98bc-416d1feb7b84`            |
| Dentro de Docker | `http://host.docker.internal:8000/v3/19217075-6d4e-4818-98bc-416d1feb7b84` |

> Para cambiar de entorno solo descomenta la línea que corresponda.

---

## Información técnica

- Framework: Spring Boot 3.x
- Cliente SOAP: JAX-WS
- Configuración: `@ConfigurationProperties` correctamente implementado
- No hay warnings ni errores en IDE


## NOTA IMPORTANTE (ANEXO):
- Dado que la url que exponen en el ejercio: https://run.mocky.io/v3/19217075-6d4e-4818-98bc-416d1feb7b84
  NO funcionó! respondio un 404, implementé en mi servidor de producción de pruebas (https://jaimediaz.dev) un endpoint que actúa como MOCK de SOAP es decir, envío el XML de la petición, el servicio responde
  con XML y el servicio de Sporingboot parsea común y corriente como lo exige el ejercicio.

- Comparto una captura de pantalla evidenciando el problema:
![Error Mocky](https://i.imgur.com/pqM0IIU.png)

- Comparto una captura de pantalla para evidenciar el funcionamiento
del servicio mockeado en mi entorno:
![Servicio que provee mi servidor](https://i.imgur.com/rMRtzJd.png)

- Comparto una captura de pantalla más, para evidenciar el correcto funcionamiento de la API implementada para la prueba desde postman en el puerto 8080:
  ![API Java Springboot funcionando](https://i.imgur.com/r22WuOP.png)