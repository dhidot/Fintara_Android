package com.bcafinance.fintara.config.network.api

import com.bcafinance.fintara.data.model.ApiResponse
import com.bcafinance.fintara.data.model.dto.customer.ChangePasswordRequest
import com.bcafinance.fintara.data.model.dto.auth.googleLogin.GoogleLoginRequest
import com.bcafinance.fintara.data.model.dto.auth.login.LoginRequest
import com.bcafinance.fintara.data.model.dto.auth.login.LoginResponse
import com.bcafinance.fintara.data.model.dto.auth.RegisterCustomerResponse
import com.bcafinance.fintara.data.model.dto.auth.RegisterRequest
import com.bcafinance.fintara.data.model.dto.auth.forgotPassword.ForgotPasswordRequest
import com.bcafinance.fintara.data.model.dto.auth.setPassword.SetPasswordRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApiService {
    @POST("/api/v1/auth/register/customer")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterCustomerResponse>

    @POST("/api/v1/auth/login-customer")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("/api/v1/auth/login-google")
    suspend fun loginWithGoogle(
        @Header("Authorization") idToken: String,
        @Body request: GoogleLoginRequest
    ): Response<LoginResponse>

    @POST("/api/v1/auth/logout")
    suspend fun logout(): Response<ApiResponse<String>>

    @POST("/api/v1/auth/set-password")
    suspend fun setPassword(
        @Body request: SetPasswordRequest
    ): ApiResponse<String>

    @PUT("/api/v1/auth/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Response<ApiResponse<Unit>>

    @POST("/api/v1/auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ):Response<ApiResponse<Unit>>
}
