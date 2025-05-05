package com.bcafinance.fintara.data.repository

import android.util.Log
import com.bcafinance.fintara.data.model.LoginRequest
import com.bcafinance.fintara.data.model.LoginResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository {

    val apiService = RetrofitClient.apiService

    fun login(request: LoginRequest, onSuccess: (String, String) -> Unit, onError: (String) -> Unit) {
        apiService.loginUser(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                // Log response JSON lengkap
                Log.d("LoginResponseRaw", Gson().toJson(response.body()))

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val token = loginResponse?.data?.data?.jwt?.token ?: ""
                    val message = loginResponse?.message ?: "Login successful"
                    onSuccess(message, token)
                } else {
                    // Log body saat error (misalnya validation error 400, dll)
                    val errorBody = response.errorBody()?.string()
                    Log.e("LoginResponseError", errorBody ?: "Unknown error")
                    onError("Login failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginNetworkError", "Failure: ${t.localizedMessage}", t)
                onError("Error: ${t.localizedMessage}")
            }
        })
    }
}
