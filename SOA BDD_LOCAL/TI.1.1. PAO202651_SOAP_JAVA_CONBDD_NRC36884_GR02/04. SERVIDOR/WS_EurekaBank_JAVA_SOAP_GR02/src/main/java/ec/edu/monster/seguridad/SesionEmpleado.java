package ec.edu.monster.seguridad;

import java.time.LocalDateTime;

public record SesionEmpleado(String codigoEmpleado, String nombreEmpleado, LocalDateTime expiracion) {
}
