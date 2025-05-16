package com.bcafinance.fintara.data.model.dto.auth.setPassword

data class SetPasswordRequest(
    val password: String,
    val confirmPassword: String
)