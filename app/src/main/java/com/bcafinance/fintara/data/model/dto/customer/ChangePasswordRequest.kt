package com.bcafinance.fintara.data.model.dto.customer

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
    val confirmNewPassword: String
)
