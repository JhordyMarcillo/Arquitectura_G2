package ec.edu.monster.controladores.manejadores;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

/** Incluye los campos nulos (por ejemplo cuentaReferencia) para cumplir el contrato. */
@Provider
public class ConfiguracionJson implements ContextResolver<Jsonb> {

    private static final Jsonb JSONB = JsonbBuilder.create(new JsonbConfig().withNullValues(true));

    @Override
    public Jsonb getContext(Class<?> tipo) {
        return JSONB;
    }
}
