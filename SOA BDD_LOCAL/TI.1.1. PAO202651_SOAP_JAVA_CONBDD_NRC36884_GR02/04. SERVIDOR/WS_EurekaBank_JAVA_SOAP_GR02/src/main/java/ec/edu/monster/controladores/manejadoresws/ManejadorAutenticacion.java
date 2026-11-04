package ec.edu.monster.controladores.manejadoresws;

import ec.edu.monster.modelos.utilidades.enums.EstadoToken;
import ec.edu.monster.seguridad.AdministradorTokens;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Set;
import javax.xml.namespace.QName;
import org.w3c.dom.NodeList;

/**
 * Lee el elemento token de cada peticion (salvo iniciarSesion) y deja su estado en el
 * contexto del mensaje para que el servicio responda AUTH-001 o AUTH-002.
 */
public class ManejadorAutenticacion implements SOAPHandler<SOAPMessageContext> {

    public static final String ESTADO_TOKEN = "ec.edu.monster.estadoToken";
    public static final String TOKEN = "ec.edu.monster.token";

    @Override
    public boolean handleMessage(SOAPMessageContext contexto) {
        Boolean esSalida = (Boolean) contexto.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (esSalida) {
            return true;
        }

        QName operacion = (QName) contexto.get(MessageContext.WSDL_OPERATION);
        if (operacion != null && "iniciarSesion".equals(operacion.getLocalPart())) {
            return true;
        }

        String token = null;
        EstadoToken estado;
        try {
            NodeList nodosToken = contexto.getMessage().getSOAPBody().getElementsByTagNameNS("*", "token");
            if (nodosToken.getLength() > 0) {
                token = nodosToken.item(0).getTextContent().trim();
            }
            estado = AdministradorTokens.validar(token);
        } catch (Exception e) {
            estado = EstadoToken.INVALIDO;
        }

        contexto.put(ESTADO_TOKEN, estado);
        contexto.setScope(ESTADO_TOKEN, MessageContext.Scope.APPLICATION);
        contexto.put(TOKEN, token);
        contexto.setScope(TOKEN, MessageContext.Scope.APPLICATION);
        return true;
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    @Override
    public boolean handleFault(SOAPMessageContext contexto) {
        return true;
    }

    @Override
    public void close(MessageContext contexto) {
    }
}
