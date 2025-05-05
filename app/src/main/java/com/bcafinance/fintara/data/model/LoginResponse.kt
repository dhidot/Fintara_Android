package com.bcafinance.fintara.data.model

data class LoginResponse(
    val status: String,
    val message: String,
    val data: LoginData
)

data class LoginData(
    val data: LoginDataDetails
)

data class LoginDataDetails(
    val jwt: Jwt
)

data class Jwt(
    val token: String,
    val type: String,
    val username: String,
    val role: String,
    val name: String,
    val features: List<String>
)
