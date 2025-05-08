package com.bcafinance.fintara.data.model.dto

data class LoginResult(
    val message: String,
    val token: String,
    val firstLogin: Boolean
)