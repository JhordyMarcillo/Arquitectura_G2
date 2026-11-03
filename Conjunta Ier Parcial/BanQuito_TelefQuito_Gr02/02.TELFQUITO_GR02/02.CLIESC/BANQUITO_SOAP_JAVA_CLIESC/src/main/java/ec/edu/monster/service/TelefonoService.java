package ec.edu.monster.service;

import ec.edu.monster.ws.Telefonos;
import ec.edu.monster.ws.WSTelefonos;
import ec.edu.monster.ws.WSTelefonos_Service;
import java.util.List;

public class TelefonoService {

    private final WSTelefonos port;

    public TelefonoService() {
        // Inicializar el cliente del web service
        WSTelefonos_Service servicio = new WSTelefonos_Service();
        this.port = servicio.getWSTelefonosPort();
    }

    /**
     * Obtiene la lista de todos los teléfonos.
     * @return Lista de objetos Telefonos
     */
    public List<Telefonos> obtenerTelefonos() {
        return port.getAllTelefonos();
    }

    /**
     * Obtiene un teléfono por su ID.
     * @param id ID del teléfono a buscar
     * @return Objeto Telefonos correspondiente al ID
     */
    public Telefonos obtenerTelefonoPorId(int id) {
        return port.getTelefonoById(id);
    }

    /**
     * Inserta un nuevo teléfono.
     * @param telefono Objeto Telefonos a insertar
     * @return Mensaje de confirmación o error
     */
    public String insertarTelefono(Telefonos telefono) {
        return port.insertTelefono(telefono);
    }

    /**
     * Actualiza la información de un teléfono existente.
     * @param telefono Objeto Telefonos con la información actualizada
     * @return Mensaje de confirmación o error
     */
    public String actualizarTelefono(Telefonos telefono) {
        return port.updateTelefono(telefono);
    }
}
