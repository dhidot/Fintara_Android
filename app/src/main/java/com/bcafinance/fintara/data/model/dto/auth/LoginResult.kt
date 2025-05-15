package com.bcafinance.fintara.data.model.dto.auth

data class LoginResult(
    val message: String,
    val token: String,
    val firstLogin: Boolean
)