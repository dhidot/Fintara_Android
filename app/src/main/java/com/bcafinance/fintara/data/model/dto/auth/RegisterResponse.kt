package com.bcafinance.fintara.data.model.dto.auth

data class RegisterCustomerResponse(
    val timestamp: String,
    val status: Int,
    val message: String,
    val data: CustomerData
)

data class CustomerData(
    val id: String,
    val email: String,
    val name: String,
    val role: String,
    val userType: String,
    val roleFeatures: List<String>,
    val plafond: String,
    val remainingPlafond: Double
)
