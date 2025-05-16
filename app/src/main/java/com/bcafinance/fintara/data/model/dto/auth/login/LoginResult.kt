package com.bcafinance.fintara.data.model.dto.auth.login

data class LoginResult(
    val message: String,
    val token: String,
    val firstLogin: Boolean,
    val hasPassword: Boolean
)