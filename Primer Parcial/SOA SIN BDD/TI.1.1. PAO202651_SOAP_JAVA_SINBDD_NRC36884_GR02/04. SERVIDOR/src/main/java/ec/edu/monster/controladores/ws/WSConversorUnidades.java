package ec.edu.monster.controladores.ws;

import ec.edu.monster.controladores.manejadoresws.ManejadorAutenticacion;
import ec.edu.monster.modelos.utilidades.enums.UnidadTemperatura;
import ec.edu.monster.modelos.utilidades.enums.UnidadMasa;
import ec.edu.monster.modelos.utilidades.enums.UnidadLongitud;
import ec.edu.monster.modelos.utilidades.enums.EstadoToken;
import ec.edu.monster.modelos.utilidades.enums.CodigoRespuesta;
import ec.edu.monster.modelos.utilidades.enums.CategoriaUnidad;
import ec.edu.monster.modelos.utilidades.ConversorTemperatura;
import ec.edu.monster.modelos.utilidades.ConversorMasa;
import ec.edu.monster.modelos.utilidades.ConversorLongitud;
import ec.edu.monster.modelos.utilidades.ExcepcionValidacion;
import ec.edu.monster.modelos.utilidades.ValidadorConversion;
import ec.edu.monster.modelos.utilidades.RespuestaAutenticacion;
import ec.edu.monster.modelos.utilidades.RespuestaCatalogo;
import ec.edu.monster.modelos.utilidades.RespuestaUnidades;
import ec.edu.monster.modelos.utilidades.RespuestaConversion;
import ec.edu.monster.servicios.ServicioConversor;
import ec.edu.monster.servicios.ServicioCatalogo;
import ec.edu.monster.modelos.utilidades.mapeadores.UnidadMapper;
import ec.edu.monster.seguridad.AdministradorCredenciales;
import ec.edu.monster.seguridad.AdministradorTokens;

import jakarta.annotation.Resource;
import jakarta.jws.HandlerChain;
import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.xml.ws.WebServiceContext;
import java.util.function.Function;

@WebService(serviceName = "WSConversorUnidades")
@HandlerChain(file = "/manejador.xml")
public class WSConversorUnidades {

    @Resource
    private WebServiceContext contextoWS;

    private final ServicioConversor<UnidadLongitud> servicioLongitud =
            new ServicioConversor<>(new ConversorLongitud());

    private final ServicioConversor<UnidadMasa> servicioMasa =
            new ServicioConversor<>(new ConversorMasa());

    private final ServicioConversor<UnidadTemperatura> servicioTemperatura =
            new ServicioConversor<>(new ConversorTemperatura());

    private final ServicioCatalogo servicioCatalogo = new ServicioCatalogo();

    @WebMethod(operationName = "autenticar")
    public RespuestaAutenticacion autenticar(
            @WebParam(name = "usuario") String usuario,
            @WebParam(name = "contrasenia") String contrasenia) {

        if (AdministradorCredenciales.validarCredenciales(usuario, contrasenia)) {
            return RespuestaAutenticacion.exito(
                    AdministradorTokens.generarToken(),
                    AdministradorTokens.getMinutosExpiracion());
        }
        return RespuestaAutenticacion.error(CodigoRespuesta.CREDENCIALES_INCORRECTAS, "Credenciales incorrectas");
    }

    @WebMethod(operationName = "obtenerCategorias")
    public RespuestaCatalogo obtenerCategorias(
            @WebParam(name = "token") String token) {

        try {
            validarToken(token);
            return RespuestaCatalogo.exito(servicioCatalogo.obtenerCategorias());
        } catch (ExcepcionValidacion e) {
            return RespuestaCatalogo.error(e.getCodigo(), e.getMessage());
        } catch (Exception e) {
            return RespuestaCatalogo.error(CodigoRespuesta.ERROR_INTERNO, "Error interno del servicio");
        }
    }

    @WebMethod(operationName = "obtenerUnidades")
    public RespuestaUnidades obtenerUnidades(
            @WebParam(name = "token") String token,
            @WebParam(name = "categoria") String categoria) {

        try {
            validarToken(token);
            CategoriaUnidad categoriaUnidad = UnidadMapper.toCategoria(categoria);
            return RespuestaUnidades.exito(categoriaUnidad.name(), servicioCatalogo.obtenerUnidades(categoriaUnidad));
        } catch (ExcepcionValidacion e) {
            return RespuestaUnidades.error(e.getCodigo(), e.getMessage(), categoria);
        } catch (Exception e) {
            return RespuestaUnidades.error(CodigoRespuesta.ERROR_INTERNO, "Error interno del servicio", categoria);
        }
    }

    @WebMethod(operationName = "convertirLongitud")
    public RespuestaConversion convertirLongitud(
            @WebParam(name = "token") String token,
            @WebParam(name = "unidadOrigen") String unidadOrigen,
            @WebParam(name = "unidadDestino") String unidadDestino,
            @WebParam(name = "valor") Double valor) {

        return procesarConversion(token, unidadOrigen, unidadDestino, valor,
                CategoriaUnidad.LONGITUD, UnidadMapper::toLongitud, servicioLongitud);
    }

    @WebMethod(operationName = "convertirMasa")
    public RespuestaConversion convertirMasa(
            @WebParam(name = "token") String token,
            @WebParam(name = "unidadOrigen") String unidadOrigen,
            @WebParam(name = "unidadDestino") String unidadDestino,
            @WebParam(name = "valor") Double valor) {

        return procesarConversion(token, unidadOrigen, unidadDestino, valor,
                CategoriaUnidad.MASA, UnidadMapper::toMasa, servicioMasa);
    }

    @WebMethod(operationName = "convertirTemperatura")
    public RespuestaConversion convertirTemperatura(
            @WebParam(name = "token") String token,
            @WebParam(name = "unidadOrigen") String unidadOrigen,
            @WebParam(name = "unidadDestino") String unidadDestino,
            @WebParam(name = "valor") Double valor) {

        return procesarConversion(token, unidadOrigen, unidadDestino, valor,
                CategoriaUnidad.TEMPERATURA, UnidadMapper::toTemperatura, servicioTemperatura);
    }

    private <T extends Enum<T>> RespuestaConversion procesarConversion(String token,
            String unidadOrigen, String unidadDestino, Double valor, CategoriaUnidad categoria,
            Function<String, T> mapeador, ServicioConversor<T> servicio) {

        try {
            validarToken(token);

            ValidadorConversion.validarValor(valor);
            ValidadorConversion.validarUnidad(unidadOrigen, "origen", categoria);
            ValidadorConversion.validarUnidad(unidadDestino, "destino", categoria);
            ValidadorConversion.validarRango(valor, categoria, unidadOrigen);

            T origen = mapeador.apply(unidadOrigen);
            T destino = mapeador.apply(unidadDestino);

            double resultado = servicio.convertir(valor, origen, destino);

            return RespuestaConversion.exito(resultado, origen.name(), destino.name());

        } catch (ExcepcionValidacion e) {
            return RespuestaConversion.error(e.getCodigo(), e.getMessage(), unidadOrigen, unidadDestino);
        } catch (Exception e) {
            return RespuestaConversion.error(CodigoRespuesta.ERROR_INTERNO, "Error interno del servicio",
                    unidadOrigen, unidadDestino);
        }
    }

    private void validarToken(String token) {

        EstadoToken estado = null;
        if (contextoWS != null) {
            estado = (EstadoToken) contextoWS.getMessageContext().get(ManejadorAutenticacion.ESTADO_TOKEN);
        }
        if (estado == null) {
            estado = AdministradorTokens.validarToken(token);
        }

        switch (estado) {
            case INVALIDO -> throw new ExcepcionValidacion(CodigoRespuesta.TOKEN_INVALIDO,
                    "Token invalido o sesion expirada");
            case EXPIRADO -> throw new ExcepcionValidacion(CodigoRespuesta.TOKEN_INVALIDO,
                    "Token invalido o sesion expirada: la sesion ha caducado, autentiquese nuevamente");
            default -> {
            }
        }
    }
}
