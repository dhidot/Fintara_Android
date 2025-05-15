package com.bcafinance.fintara.config.network.api

import com.bcafinance.fintara.data.model.ApiResponse
import com.bcafinance.fintara.data.model.Plafond
import retrofit2.Call
import retrofit2.http.GET

interface PlafondApiService {

    @GET("api/v1/plafonds/all") // Sesuaikan dengan endpoint yang ada di backend
    fun getAllPlafonds(): Call<ApiResponse<List<Plafond>>>
}