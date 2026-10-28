package ec.edu.monster.model

data class ConversionRequest(
    val valor: Double,
    val unidadOrigen: String,
    val unidadDestino: String,
    val categoria: String
)

data class ConversionResponse(
    val exito: Boolean? = null,
    val valorConvertido: Double? = null,
    val resultado: Double? = null,
    val mensaje: String? = null
)

data class LoginRequest(
    val usuario: String,
    val contrasena: String
)

data class LoginResponse(
    val autenticado: Boolean? = null,
    val mensaje: String? = null
)



