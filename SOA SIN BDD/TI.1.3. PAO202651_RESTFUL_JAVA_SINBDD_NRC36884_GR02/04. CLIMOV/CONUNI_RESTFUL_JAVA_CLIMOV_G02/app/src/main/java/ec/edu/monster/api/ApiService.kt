package ec.edu.monster.api

import ec.edu.monster.model.ConversionRequest
import ec.edu.monster.model.ConversionResponse
import ec.edu.monster.model.LoginRequest
import ec.edu.monster.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    // Base path: /CONUNI_RESTFUL_JAVA_GR02/api/conversion
    @POST("/CONUNI_RESTFUL_JAVA_GR02/api/conversion/convertir")
    suspend fun convertir(@Body body: ConversionRequest): Response<ConversionResponse>

    @POST("/CONUNI_RESTFUL_JAVA_GR02/api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>
}



