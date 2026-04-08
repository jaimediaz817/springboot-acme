package com.pruebajdiaz.acme.pedidos.service;

import java.security.cert.X509Certificate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.pruebajdiaz.acme.pedidos.model.PedidoRequest;
import com.pruebajdiaz.acme.pedidos.model.PedidoResponse;

@Service
public class PedidoService {
    /** URL del servicio SOAP externo */
    @Value("${acme.soap.url}")
    private String serviceUrl;

    /**
     * procesa el pedido: convierte la solicitud JSON a XML, envía la solicitud SOAP
     * al servicio externo,
     * 
     * @param request
     * @return
     */
    public PedidoResponse procesarPedido(PedidoRequest request) {

        // de JSON a XML
        String xmlRequest = construirXml(request);

        // mandar a llamar al servicio SOAP
        String xmlResponse = getServicioSoap(xmlRequest);

        // transforma/parsea de XML a JSON
        return parsearRespuesta(xmlResponse);
    }

    /**
     * construye la solicitud XML para el servicio SOAP a partir del objeto
     * PedidoRequest
     * 
     * @param req
     * @return
     */
    private String construirXml(PedidoRequest req) {
        var pedido = req.getEnviarPedido();
        return """
                <soapenv:Envelope
                    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                    xmlns:env="http://WSDLs/EnvioPedidos/EnvioPedidosAcme">
                    <soapenv:Header/>
                    <soapenv:Body>
                        <env:EnvioPedidoAcme>
                            <EnvioPedidoRequest>
                                <pedido>%s</pedido>
                                <Cantidad>%s</Cantidad>
                                <EAN>%s</EAN>
                                <Producto>%s</Producto>
                                <Cedula>%s</Cedula>
                                <Direccion>%s</Direccion>
                            </EnvioPedidoRequest>
                        </env:EnvioPedidoAcme>
                    </soapenv:Body>
                </soapenv:Envelope>
                """.formatted(
                pedido.getNumPedido(),
                pedido.getCantidadPedido(),
                pedido.getCodigoEAN(),
                pedido.getNombreProducto(),
                pedido.getNumDocumento(),
                pedido.getDireccion());
    }

    /**
     * envía la solicitud SOAP al servicio externo y devuelve la respuesta XML como
     * cadena
     * 
     * @param xmlRequest
     * @return
     */
    private String getServicioSoap(String xmlRequest) {
        // ✅ IMPRIME EL XML QUE ESTAS ENVIANDO EXACTAMENTE
        System.out.println("\n📤 ENVIANDO PETICION XML:");
        System.out.println("=".repeat(100));
        System.out.println(xmlRequest);
        System.out.println("=".repeat(100));

        RestTemplate restTemplate = getRestTemplateInseguro();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);

        // Header OBLIGATORIO para casi todos los servidores SOAP
        // Muchos servicios devuelven 404 o 500 SILENCIOSAMENTE si no envias este
        // header, incluso vacio
        headers.set("SOAPAction", "\"\"");

        HttpEntity<String> entity = new HttpEntity<>(xmlRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                serviceUrl,
                HttpMethod.POST,
                entity,
                String.class);

        // IMPRIME LA RESPUESTA QUE RECIBES (incluso si es 404/error)
        System.out.println("\n RESPUESTA RECIBIDA:");
        System.out.println("Codigo HTTP: " + response.getStatusCode());
        System.out.println("=".repeat(100));
        System.out.println(response.getBody() != null ? response.getBody() : "CUERPO VACIO");
        System.out.println("=".repeat(100));

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error llamando servicio SOAP. Codigo HTTP: " + response.getStatusCode());
        }

        return response.getBody();
    }

    /**
     * crea un RestTemplate que acepta certificados SSL sin validación
     * SOLO PARA ENTORNOS DE PRUEBAS LOCALES O DE DESARROLLO, NUNCA USAR EN
     * PRODUCCIÓN
     * 
     * @return
     */
    private RestTemplate getRestTemplateInseguro() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            // Retornar array vacío en lugar de null (buena práctica)
                            return new X509Certificate[0];
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            // Implementación vacía intencionalmente para entorno PRUEBAS
                            // Acepta todos los certificados cliente sin validación
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            // Implementación vacía intencionalmente para entorno PRUEBAS
                            // Acepta todos los certificados servidor sin validación
                            // NUNCA utilizar esta configuración en PRODUCCIÓN
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            return new RestTemplate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * parsea la respuesta XML del servicio SOAP y extrae el código de envío y el
     * estado
     * 
     * @param xml
     * @return
     */
    private PedidoResponse parsearRespuesta(String xml) {
        // Extraer Codigo y Mensaje del XML
        String codigo = extraerTag(xml, "Codigo");
        String mensaje = extraerTag(xml, "Mensaje");

        PedidoResponse response = new PedidoResponse();
        PedidoResponse.EnviarPedidoRespuesta respuesta = new PedidoResponse.EnviarPedidoRespuesta();
        respuesta.setCodigoEnvio(codigo);
        respuesta.setEstado(mensaje);
        response.setEnviarPedidoRespuesta(respuesta);

        return response;
    }

    /**
     * 
     * @param xml
     * @param tag
     * @return el valor del tag, o cadena vacía si no se encuentra o si el XML/tag
     *         es nulo
     */
    private String extraerTag(String xml, String tag) {
        if (xml == null || tag == null) {
            return "";
        }

        String apertura = "<" + tag + ">";
        String cierre = "</" + tag + ">";

        int inicio = xml.indexOf(apertura);
        if (inicio == -1)
            return "";

        int fin = xml.indexOf(cierre, inicio);
        if (fin == -1)
            return "";

        return xml.substring(inicio + apertura.length(), fin).trim();
    }
}
