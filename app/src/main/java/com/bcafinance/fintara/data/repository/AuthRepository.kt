package com.bcafinance.fintara.data.repository

import android.util.Log
import com.bcafinance.fintara.config.network.SessionManager
import com.bcafinance.fintara.data.model.dto.LoginRequest
import com.bcafinance.fintara.data.model.dto.LoginResponse
import com.bcafinance.fintara.data.model.dto.RegisterRequest
import com.bcafinance.fintara.utils.parseApiError
import retrofit2.HttpException
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
                val userName = loginResponse?.data?.jwt?.username ?: "Unknown"

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

    suspend fun loginWithGoogle(idToken: String): Result<Pair<String, Boolean>> {
        return try {
            val response = apiService.loginWithGoogle(idToken)
            val loginResponse = response.body()
            if (response.isSuccessful) {
                val loginResponse = response.body()
                val token = loginResponse?.data?.jwt?.token ?: ""
                val message = loginResponse?.message ?: "Login successful"
                val firstLogin = loginResponse?.data?.firstLogin ?: false
                val userName = loginResponse?.data?.jwt?.username ?: "Unknown"

                // Kembalikan token dan status login pertama kali
                Result.success(token to firstLogin)
            } else {
                Result.failure(Exception("Login failed: "))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("LoginError", "HTTP ${e.code()}, body: $errorBody", e)
            Result.failure(Exception("HTTP ${e.code()} - $errorBody"))
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception during login: ${e.localizedMessage}", e)
            Result.failure(e)
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


