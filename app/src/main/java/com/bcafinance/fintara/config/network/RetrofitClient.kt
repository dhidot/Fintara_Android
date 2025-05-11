import android.content.Context
import com.bcafinance.fintara.config.network.AuthApiService
import com.bcafinance.fintara.config.network.CustomerApiService
import com.bcafinance.fintara.config.network.PlafondApiService
import com.bcafinance.fintara.config.network.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Ganti dengan URL API kamu
    //https://d7aa-114-124-209-179.ngrok-free.app
    //http://34.28.17.46/
    private const val BASE_URL = "https://055f-182-1-105-130.ngrok-free.app"
    private lateinit var retrofit: Retrofit

    // Service instances
    val authApiService: AuthApiService by lazy { retrofit.create(AuthApiService::class.java) }
    val customerApiService: CustomerApiService by lazy { retrofit.create(CustomerApiService::class.java) }
    val plafondApiService: PlafondApiService by lazy { retrofit.create(PlafondApiService::class.java) }

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
