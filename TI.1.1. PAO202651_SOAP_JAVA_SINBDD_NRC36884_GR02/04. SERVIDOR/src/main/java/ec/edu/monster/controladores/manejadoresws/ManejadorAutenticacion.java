package ec.edu.monster.controladores.manejadoresws;

import ec.edu.monster.modelos.utilidades.enums.EstadoToken;
import ec.edu.monster.seguridad.AdministradorTokens;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;
import jakarta.xml.ws.handler.MessageContext;

import javax.xml.namespace.QName;
import org.w3c.dom.NodeList;
import java.util.Set;

public class ManejadorAutenticacion implements SOAPHandler<SOAPMessageContext> {

    public static final String ESTADO_TOKEN = "ec.edu.monster.estadoToken";

    @Override
    public boolean handleMessage(SOAPMessageContext contexto) {

        Boolean esSalida = (Boolean) contexto.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (!esSalida) {
            QName operacion = (QName) contexto.get(MessageContext.WSDL_OPERATION);
            if (operacion != null && "autenticar".equals(operacion.getLocalPart())) {
                return true;
            }

            EstadoToken estado;
            try {
                var mensajeSOAP = contexto.getMessage();
                var cuerpo = mensajeSOAP.getSOAPBody();

                NodeList nodosToken = cuerpo.getElementsByTagNameNS("*", "token");

                if (nodosToken.getLength() == 0) {
                    estado = EstadoToken.INVALIDO;
                } else {
                    estado = AdministradorTokens.validarToken(nodosToken.item(0).getTextContent());
                }

            } catch (Exception e) {
                estado = EstadoToken.INVALIDO;
            }

            contexto.put(ESTADO_TOKEN, estado);
            contexto.setScope(ESTADO_TOKEN, MessageContext.Scope.APPLICATION);
        }
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
    public void close(MessageContext contexto) {}
}
