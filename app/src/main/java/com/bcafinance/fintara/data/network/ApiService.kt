package com.bcafinance.fintara.data.network

import com.bcafinance.fintara.data.model.RegisterCustomerResponse
import com.bcafinance.fintara.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/v1/auth/register/customer")
    suspend fun registerCustomer(@Body request: RegisterRequest): Response<RegisterCustomerResponse>
}
