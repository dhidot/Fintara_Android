package com.bcafinance.fintara.data.model.dto.auth

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
    val confirmNewPassword: String
)
