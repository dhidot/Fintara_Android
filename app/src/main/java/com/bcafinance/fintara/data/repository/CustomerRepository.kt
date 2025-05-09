package com.bcafinance.fintara.data.repository

import android.content.Context
import com.bcafinance.fintara.config.network.CustomerApiService
import com.bcafinance.fintara.data.model.ApiResponse
import com.bcafinance.fintara.data.model.dto.FirstTimeUpdateRequest
import com.bcafinance.fintara.data.model.dto.UserWithCustomerResponse
import com.bcafinance.fintara.config.network.SessionManager
import android.util.Log
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class CustomerRepository(private val customerApiService:CustomerApiService) {
    suspend fun getProfile(): ApiResponse<UserWithCustomerResponse> {
        return customerApiService.getMyProfile()
    }

    suspend fun updateFirstTimeProfile(request: FirstTimeUpdateRequest): ApiResponse<String> {
        return customerApiService.updateFirstTimeProfile(request)
    }
}

