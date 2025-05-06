package com.bcafinance.fintara.config.network

import com.bcafinance.fintara.data.model.LoginRequest
import com.bcafinance.fintara.data.model.LoginResponse
import com.bcafinance.fintara.data.model.RegisterCustomerResponse
import com.bcafinance.fintara.data.model.RegisterRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/v1/auth/register/customer")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterCustomerResponse>

    @POST("api/v1/auth/login-customer")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<LoginResponse>
}
