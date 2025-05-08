package com.bcafinance.fintara.data.model.dto

data class PlafondResponse(
    val id: String,
    val name: String,
    val maxAmount: Double,
    val interestRate: Double,
    val minTenor: Int,
    val maxTenor: Int
)