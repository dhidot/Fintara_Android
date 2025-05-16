package com.bcafinance.fintara.data.model.dto.auth.googleLogin

data class GoogleLoginResult(
    val message: String,
    val token: String,
    val firstLogin: Boolean,
    val hasPassword: Boolean
)