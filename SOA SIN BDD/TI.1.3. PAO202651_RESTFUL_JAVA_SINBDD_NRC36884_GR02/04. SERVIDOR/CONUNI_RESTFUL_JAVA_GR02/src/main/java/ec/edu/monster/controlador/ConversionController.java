package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Conversion;
import ec.edu.monster.modelo.ConversionRequest;
import ec.edu.monster.modelo.ConversionResponse;
import ec.edu.monster.servicios.ConversionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Controlador REST para las conversiones de unidades
 * Maneja todas las peticiones HTTP y expone los endpoints RESTful
 * La lógica de conversión reside en ConversionService (bean CDI inyectado)
 * @author ACER NITRO V15
 */
@Path("/conversion")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConversionController {

    @Inject
    private ConversionService conversionService;
    
    // ========== ENDPOINTS GENERALES ==========
    
    /**
     * Endpoint base para mostrar información del servicio
     * GET /api/conversion/
     */
    @GET
    @Path("/")
    public Response obtenerInfoBase() {
        String info = "Monsters Inc. Converter RESTful API v1.0 - Powered by Sullivan's Energy\n\n" +
                     "Endpoints disponibles:\n" +
                     "- GET /api/conversion/info - Información del servicio\n" +
                     "- GET /api/conversion/temperatura/* - Conversiones de temperatura\n" +
                     "- GET /api/conversion/longitud/* - Conversiones de longitud\n" +
                     "- GET /api/conversion/peso/* - Conversiones de peso/masa\n" +
                     "- POST /api/conversion/convertir - Conversión genérica\n\n" +
                     "Ejemplo: GET /api/conversion/temperatura/celsius-to-fahrenheit?celsius=100";
        return Response.ok(info).build();
    }
    
    /**
     * Endpoint principal para conversiones genéricas
     * POST /api/conversion/convertir
     */
    @POST
    @Path("/convertir")
    public Response convertir(ConversionRequest request) {
        try {
            if (request == null
                    || request.getCategoria() == null
                    || request.getUnidadOrigen() == null
                    || request.getUnidadDestino() == null) {
                ConversionResponse errorResponse = ConversionResponse.crearError("Solicitud inválida o incompleta");
                return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
            }

            ConversionResponse response = conversionService.convertir(
                request.getValor(), request.getUnidadOrigen(), request.getUnidadDestino(), request.getCategoria()
            );
            if (!response.isExito()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
            }
            return Response.ok(response).build();
        } catch (Exception e) {
            ConversionResponse errorResponse = ConversionResponse.crearError("Error interno: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
    }
    
    /**
     * Endpoint para obtener información del servicio
     * GET /api/conversion/info
     */
    @GET
    @Path("/info")
    public Response obtenerInfo() {
        String info = "Monsters Inc. Converter RESTful API v1.0 - Powered by Sullivan's Energy";
        return Response.ok(info).build();
    }
    
    /**
     * Endpoint para obtener lista de endpoints disponibles
     * GET /api/conversion/endpoints
     */
    @GET
    @Path("/endpoints")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerEndpoints() {
        try {
            String endpoints = "{\n" +
                "  \"servicio\": \"Monsters Inc. Converter RESTful API\",\n" +
                "  \"version\": \"1.0\",\n" +
                "  \"baseUrl\": \"http://localhost:8080/CONUNI_RESTFUL_JAVA_GR09/api/conversion\",\n" +
                "  \"endpoints\": {\n" +
                "    \"temperatura\": [\n" +
                "      \"GET /temperatura/celsius-to-fahrenheit?celsius=100\",\n" +
                "      \"GET /temperatura/fahrenheit-to-celsius?fahrenheit=212\",\n" +
                "      \"GET /temperatura/celsius-to-kelvin?celsius=0\",\n" +
                "      \"GET /temperatura/kelvin-to-celsius?kelvin=273.15\",\n" +
                "      \"GET /temperatura/fahrenheit-to-kelvin?fahrenheit=32\",\n" +
                "      \"GET /temperatura/kelvin-to-fahrenheit?kelvin=273.15\"\n" +
                "    ],\n" +
                "    \"longitud\": [\n" +
                "      \"GET /longitud/metros-to-pies?metros=1\",\n" +
                "      \"GET /longitud/pies-to-metros?pies=3.28084\",\n" +
                "      \"GET /longitud/metros-to-pulgadas?metros=1\",\n" +
                "      \"GET /longitud/pulgadas-to-metros?pulgadas=39.3701\",\n" +
                "      \"GET /longitud/kilometros-to-millas?kilometros=1\",\n" +
                "      \"GET /longitud/millas-to-kilometros?millas=0.621371\"\n" +
                "    ],\n" +
                "    \"peso\": [\n" +
                "      \"GET /peso/kilogramos-to-libras?kilogramos=1\",\n" +
                "      \"GET /peso/libras-to-kilogramos?libras=2.20462\",\n" +
                "      \"GET /peso/gramos-to-onzas?gramos=28.3495\",\n" +
                "      \"GET /peso/onzas-to-gramos?onzas=1\"\n" +
                "    ],\n" +
                "    \"generico\": [\n" +
                "      \"POST /convertir\"\n" +
                "    ]\n" +
                "  }\n" +
                "}";
            return Response.ok(endpoints).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Error al obtener endpoints: " + e.getMessage() + "\"}")
                .build();
        }
    }
    
    // ========== ENDPOINTS DE TEMPERATURA ==========
    
    /**
     * Convierte Celsius a Fahrenheit
     * GET /api/conversion/temperatura/celsius-to-fahrenheit?celsius=100
     */
    @GET
    @Path("/temperatura/celsius-to-fahrenheit")
    public Response celsiusAFahrenheit(@QueryParam("celsius") double celsius) {
        try {
            double resultado = conversionService.celsiusAFahrenheit(celsius);
            
            ConversionResponse response = ConversionResponse.crearExito(
                celsius, resultado, Conversion.CELSIUS, Conversion.FAHRENHEIT, Conversion.CATEGORIA_TEMPERATURA
            );
            return Response.ok(response).build();
        } catch (Exception e) {
            ConversionResponse errorResponse = ConversionResponse.crearError("Error en conversión: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }
    }
    
    /**
     * Convierte Fahrenheit a Celsius
     * GET /api/conversion/temperatura/fahrenheit-to-celsius?fahrenheit=212
     */
    @GET
    @Path("/temperatura/fahrenheit-to-celsius")
    public Response fahrenheitACelsius(@QueryParam("fahrenheit") double fahrenheit) {
        try {
            double resultado = conversionService.fahrenheitACelsius(fahrenheit);
            
            ConversionResponse response = ConversionResponse.crearExito(
                fahrenheit, resultado, Conversion.FAHRENHEIT, Conversion.CELSIUS, Conversion.CATEGORIA_TEMPERATURA
            );
            return Response.ok(response).build();
        } catch (Exception e) {
            ConversionResponse errorResponse = ConversionResponse.crearError("Error en conversión: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }
    }
    
    /**
     * Convierte Celsius a Kelvin
     * GET /api/conversion/temperatura/celsius-to-kelvin?celsius=0
     */
    @GET
    @Path("/temperatura/celsius-to-kelvin")
    public Response celsiusAKelvin(@QueryParam("celsius") double celsius) {
        try {
            double resultado = conversionService.celsiusAKelvin(celsius);
            
            ConversionResponse response = ConversionResponse.crearExito(
                celsius, resultado, Conversion.CELSIUS, Conversion.KELVIN, Conversion.CATEGORIA_TEMPERATURA
            );
            return Response.ok(response).build();
        } catch (Exception e) {
            ConversionResponse errorResponse = ConversionResponse.crearError("Error en conversión: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }
    }
    
    /**
     * Convierte Kelvin a Celsius
     * GET /api/conversion/temperatura/kelvin-to-celsius?kelvin=273.15
     */
    @GET
    @Path("/temperatura/kelvin-to-celsius")
    public Response kelvinACelsius(@QueryParam("kelvin") double kelvin) {
        try {
            double resultado = conversionService.kelvinACelsius(kelvin);
            
            ConversionResponse response = ConversionResponse.crearExito(
                kelvin, resultado, Conversion.KELVIN, Conversion.CELSIUS, Conversion.CATEGORIA_TEMPERATURA
            );
            return Response.ok(response).build();
        } catch (Exception e) {
            ConversionResponse errorResponse = ConversionResponse.crearError("Error en conversión: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }
    }
    
    /**
     * Convierte Fahrenheit a Kelvin
     * GET /api/conversion/temperatura/fahrenheit-to-kelvin?fahrenheit=32
     */
    @GET
    @Path("/temperatura/fahrenheit-to-kelvin")
    public Response fahrenheitAKelvin(@QueryParam("fahrenheit") double fahrenheit) {
        try {
            double resultado = conversionService.fahrenheitAKelvin(fahrenheit);
            
            ConversionResponse response = ConversionResponse.crearExito(
                fahrenheit, resultado, Conversion.FAHRENHEIT, Conversion.KELVIN, Conversion.CATEGORIA_TEMPERATURA
            );
            return Response.ok(response).build();
        } catch (Exception e) {
            ConversionResponse errorResponse = ConversionResponse.crearError("Error en conversión: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }
    }
    
    /**
     * Convierte Kelvin a Fahrenheit
     * GET /api/conversion/temperatura/kelvin-to-fahrenheit?kelvin=273.15
     */
    @GET
    @Path("/temperatura/kelvin-to-fahrenheit")
    public Response kelvinAFahrenheit(@QueryParam("kelvin") double kelvin) {
        try {
            double resultado = conversionService.kelvinAFahrenheit(kelvin);
            
            ConversionResponse response = ConversionResponse.crearExito(
                kelvin, resultado, Conversion.KELVIN, Conversion.FAHRENHEIT, Conversion.CATEGORIA_TEMPERATURA
            );
            return Response.ok(response).build();
        } catch (Exception e) {
            ConversionResponse errorResponse = ConversionResponse.crearError("Error en conversión: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }
    }
}
