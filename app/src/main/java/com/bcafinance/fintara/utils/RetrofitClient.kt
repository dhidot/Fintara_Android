import com.bcafinance.fintara.config.network.AuthApiService
import com.bcafinance.fintara.config.network.CustomerApiService
import com.bcafinance.fintara.config.network.PlafondApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://34.28.17.46/"
    // Ganti dengan URL API kamu
    //
    //https://be01-182-3-50-180.ngrok-free.app

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Set connect timeout
        .readTimeout(30, TimeUnit.SECONDS)    // Set read timeout
        .writeTimeout(30, TimeUnit.SECONDS)   // Set write timeout
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client) // Use custom client
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApiService: AuthApiService = retrofit.create(AuthApiService::class.java)
    val customerService: CustomerApiService by lazy { retrofit.create(CustomerApiService::class.java) }
    val plafondApiService: PlafondApiService by lazy { retrofit.create(PlafondApiService::class.java) }
    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

}
