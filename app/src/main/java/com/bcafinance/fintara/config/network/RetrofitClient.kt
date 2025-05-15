package com.bcafinance.fintara.config.network

import android.content.Context
import com.bcafinance.fintara.config.network.api.AuthApiService
import com.bcafinance.fintara.config.network.api.CustomerApiService
import com.bcafinance.fintara.config.network.api.PlafondApiService
import com.bcafinance.fintara.config.network.api.LoanApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Ganti dengan URL API kamu
    //https://d7aa-114-124-209-179.ngrok-free.app
    //http://34.55.162.194/
    private const val BASE_URL = "https://1ba8-114-124-238-82.ngrok-free.app"
    private lateinit var retrofit: Retrofit

    // Service instances
    val authApiService: AuthApiService by lazy { retrofit.create(AuthApiService::class.java) }
    val customerApiService: CustomerApiService by lazy { retrofit.create(CustomerApiService::class.java) }
    val plafondApiService: PlafondApiService by lazy { retrofit.create(PlafondApiService::class.java) }
    val loanApiService: LoanApiService by lazy { retrofit.create(LoanApiService::class.java) }

    fun init(context: Context) {
        val sessionManager = SessionManager(context)

        val authInterceptor = Interceptor { chain ->
            val token = sessionManager.getToken()
            val request = chain.request().newBuilder().apply {
                if (!token.isNullOrEmpty()) {
                    addHeader("Authorization", "Bearer $token")
                }
            }.build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
