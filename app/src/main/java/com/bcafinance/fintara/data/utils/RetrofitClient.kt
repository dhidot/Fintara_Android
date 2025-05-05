import com.bcafinance.fintara.data.network.ApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://34.28.17.46/" // Ganti dengan URL API kamu

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

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
