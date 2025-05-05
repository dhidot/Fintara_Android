package com.bcafinance.fintara.utils

import com.bcafinance.fintara.data.model.ApiResponse
import com.google.gson.Gson
import okhttp3.ResponseBody

inline fun <reified T> ResponseBody?.parseApiError(): ApiResponse<T>? {
    return try {
        this?.string()?.let { json ->
            Gson().fromJson(json, ApiResponse::class.java) as ApiResponse<T>
        }
    } catch (e: Exception) {
        null
    }
}
