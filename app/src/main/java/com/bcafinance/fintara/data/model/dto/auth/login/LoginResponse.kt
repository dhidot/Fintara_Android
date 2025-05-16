package com.bcafinance.fintara.data.model.dto.auth.login

data class LoginResponse(
    val status: String,
    val message: String,
    val data: LoginData
)

data class LoginData(
    val firstLogin: Boolean,
    val jwt: Jwt,
    val hasPassword: Boolean
)

data class Jwt(
    val token: String,
    val type: String,
    val username: String,
    val role: String,
    val name: String,
    val features: List<String>
)