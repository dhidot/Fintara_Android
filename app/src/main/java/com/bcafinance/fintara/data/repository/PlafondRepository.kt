package com.bcafinance.fintara.data.repository

import android.util.Log
import com.bcafinance.fintara.config.network.PlafondApiService
import com.bcafinance.fintara.data.model.ApiResponse
import com.bcafinance.fintara.data.model.Plafond
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlafondRepository {

    private val plafondApiService = RetrofitClient.plafondApiService

    // Fungsi untuk mengambil data plafon dari backend
    fun getPlafonds(callback: (List<Plafond>?, Throwable?) -> Unit) {
        plafondApiService.getAllPlafonds().enqueue(object : Callback<ApiResponse<List<Plafond>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<Plafond>>>,
                response: Response<ApiResponse<List<Plafond>>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()?.data
                    Log.d("Repository", "Plafonds: $result")
                    callback(result, null)
                } else {
                    callback(null, Throwable("Gagal: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Plafond>>>, t: Throwable) {
                callback(null, t)
            }
        })
    }
}
