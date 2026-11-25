/**
 * Mensajes del servicio SOAP. Todos los elementos van calificados en el espacio de nombres
 * del contrato, igual que los DataContract de WCF del TI.1.2, para que los clientes de
 * ambos servidores sean intercambiables.
 */
@XmlSchema(namespace = EspacioNombres.WS, elementFormDefault = XmlNsForm.QUALIFIED)
package ec.edu.monster.modelos.utilidades;

import jakarta.xml.bind.annotation.XmlNsForm;
import jakarta.xml.bind.annotation.XmlSchema;
