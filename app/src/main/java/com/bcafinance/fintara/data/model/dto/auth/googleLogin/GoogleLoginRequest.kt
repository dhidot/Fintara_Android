package com.bcafinance.fintara.data.model.dto.auth.googleLogin

data class GoogleLoginRequest(
    val fcmToken: String,
    val deviceInfo: String
)
