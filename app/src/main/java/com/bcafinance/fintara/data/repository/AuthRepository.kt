package com.bcafinance.fintara.data.repository

import RetrofitClient.apiService
import android.util.Log
import com.bcafinance.fintara.data.model.LoginRequest
import com.bcafinance.fintara.data.model.LoginResponse
import com.bcafinance.fintara.data.model.RegisterCustomerResponse
import com.bcafinance.fintara.data.model.RegisterRequest
import com.bcafinance.fintara.utils.parseApiError
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository {

    val apiService = RetrofitClient.apiService

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


    suspend fun login(request: LoginRequest): Result<Pair<String, String>> {
        return try {
            Log.d("UserRepository", "Sending login request: $request")
            val response = apiService.loginUser(request)
            Log.d("UserRepository", "Received response: $response")

            if (response.isSuccessful) {
                val loginResponse = response.body()
                val token = loginResponse?.data?.data?.jwt?.token ?: ""
                val message = loginResponse?.message ?: "Login successful"
                Log.d("UserRepository", "Login successful, token: $token")
                Result.success(Pair(message, token))
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


