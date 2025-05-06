package com.bcafinance.fintara.utils

import com.bcafinance.fintara.data.model.ApiResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody

inline fun <reified T> ResponseBody?.parseApiError(): ApiResponse<T>? {
    return try {
        this?.string()?.let { json ->
            val type = object : TypeToken<ApiResponse<T>>() {}.type
            Gson().fromJson<ApiResponse<T>>(json, type)
        }
    } catch (e: Exception) {
        null
    }
}
