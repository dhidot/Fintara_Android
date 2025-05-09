package com.bcafinance.fintara.data.model.dto

import com.bcafinance.fintara.data.model.CustomerDetails

data class UserWithCustomerResponse(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val customerDetails: CustomerDetails?
)

