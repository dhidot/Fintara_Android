package com.bcafinance.fintara.data.model.dto.auth.login

data class LoginRequest(
    val email: String,
    val password: String,
    val fcmToken: String? = null,
    val deviceInfo: String? = null
)

