package com.mod6.ae2_abp1_apinews.data.remote

import com.mod6.ae2_abp1_apinews.data.model.NewsResponse
import com.mod6.ae2_abp1_apinews.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// La URL base es: https://newsapi.org/v2/

interface NewsApiService {
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = Constants.DEFAULT_COUNTRY,
        @Query("apiKey") apiKey: String   = Constants.API_KEY
    ): Response<NewsResponse>
}

// Singleton para configurar Retrofit
object RetrofitClient {
    //private const val BASE_URL = "https://newsapi.org/v2/"

    val apiService: NewsApiService by lazy {
        // Configuramos Retrofit
        val retrofit = retrofit2.Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()

        retrofit.create(NewsApiService::class.java)
    }
}