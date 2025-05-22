package com.bcafinance.fintara.data.model.dto.auth

data class RegisterCustomerResponse(
    val id: String,
    val email: String,
    val name: String,
    val role: String,
    val userType: String,
    val roleFeatures: List<String>,
    val plafond: String,
    val remainingPlafond: Double
)