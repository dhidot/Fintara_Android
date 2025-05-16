package com.bcafinance.fintara.data.repository

import android.util.Log
import com.bcafinance.fintara.config.network.RetrofitClient
import com.bcafinance.fintara.data.model.ApiResponse
import com.bcafinance.fintara.data.model.dto.auth.ChangePasswordRequest
import com.bcafinance.fintara.data.model.dto.auth.googleLogin.GoogleLoginRequest
import com.bcafinance.fintara.data.model.dto.auth.googleLogin.GoogleLoginResponse
import com.bcafinance.fintara.data.model.dto.auth.login.LoginRequest
import com.bcafinance.fintara.data.model.dto.auth.login.LoginResponse
import com.bcafinance.fintara.data.model.dto.auth.RegisterRequest
import com.bcafinance.fintara.data.model.dto.auth.setPassword.SetPasswordRequest
import com.bcafinance.fintara.data.model.dto.auth.setPassword.SetPasswordResponse
import com.bcafinance.fintara.utils.parseApiError
import retrofit2.Response

class AuthRepository {

    val apiService = RetrofitClient.authApiService

    // Fungsi register dengan coroutine
    suspend fun register(
        request: RegisterRequest,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val response = apiService.registerUser(request)
            if (response.isSuccessful) {
                onSuccess("Registration successful!")
            } else {
                val apiError = response.errorBody()?.parseApiError<Any>()
                val message = apiError?.message ?: "Unknown error"
                onError(message.toString())
            }
        } catch (e: Exception) {
            onError("Network error: ${e.message}")
        }
    }

    suspend fun login(request: LoginRequest): Result<Triple<String, String, Boolean>> {
        return try {
            Log.d("UserRepository", "Sending login request: $request")
            val response = apiService.loginUser(request)
            Log.d("UserRepository", "Received response: $response")

            if (response.isSuccessful) {
                val loginResponse = response.body()
                val token = loginResponse?.data?.jwt?.token ?: ""
                val message = loginResponse?.message ?: "Login successful"
                val firstLogin = loginResponse?.data?.firstLogin ?: false

                Log.d("UserRepository", "Login successful, token: $token, firstLogin: $firstLogin")
                // Return message, token, and firstLogin status
                Result.success(Triple(message, token, firstLogin))
            } else {
                val errorMessage = parseErrorBody(response)
                Log.e("UserRepository", "Login failed, error: $errorMessage")
                Result.failure(Exception("Login failed: $errorMessage"))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception during login: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }


    suspend fun loginWithGoogle(idToken: String, fcmToken: String, deviceInfo: String): Result<GoogleLoginResponse> {
        return try {
            val request = GoogleLoginRequest(fcmToken, deviceInfo)
            val response = apiService.loginWithGoogle(idToken, request)

            if (response.isSuccessful) {
                val body = response.body()
                val loginData = body?.data
                val jwt = loginData?.jwt?.token ?: ""
                val firstLogin = loginData?.firstLogin ?: false
                val hasPassword = loginData?.hasPassword ?: true
                val message = body?.message ?: "Login Google berhasil"

                Result.success(GoogleLoginResponse(message, jwt, firstLogin, hasPassword))
            } else {
                val error = response.errorBody()?.string() ?: "Terjadi kesalahan"
                Result.failure(Exception("Login Google gagal: $error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setPassword(request: SetPasswordRequest): Result<ApiResponse<String>> {
        return try {
            val response = apiService.setPassword(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(request: ChangePasswordRequest): ApiResponse<Unit> {
        val response = apiService.changePassword(request)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Response body kosong")
        } else {
            val errorMsg = response.errorBody()?.string() ?: "Terjadi kesalahan"
            throw Exception(errorMsg)
        }
    }

    suspend fun logout(): Result<String> {
        return try {
            val response = apiService.logout()
            if (response.isSuccessful) {
                Result.success(response.body()?.message?.toString() ?: "Logout berhasil")
            } else {
                val error = response.errorBody()?.string()
                Result.failure(Exception("Logout gagal: $error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseErrorBody(response: Response<LoginResponse>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            if (!errorBody.isNullOrBlank()) {
                "Error: $errorBody"
            } else {
                response.message() // Jika tidak ada error body, gunakan message dari respons
            }
        } catch (e: Exception) {
            "Unknown error"
        }
    }
}


