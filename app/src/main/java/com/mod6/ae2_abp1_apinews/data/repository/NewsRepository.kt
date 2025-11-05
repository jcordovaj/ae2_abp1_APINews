package com.mod6.ae2_abp1_apinews.data.repository

import android.util.Log
import com.mod6.ae2_abp1_apinews.data.model.Article
import com.mod6.ae2_abp1_apinews.data.remote.RetrofitClient

// Gestiona la lógica de la fuente de datos.
class NewsRepository(private val apiService: com.mod6.ae2_abp1_apinews.data.remote.NewsApiService) {

    private val TAG = "NewsRepository"

    // Función suspendida para llamar al servicio REST
    suspend fun getTopHeadlines(): Result<List<Article>> {
        return try {
            val response = apiService.getTopHeadlines()

            if (response.isSuccessful) {
                // Mapeamos los DTOs al modelo, si la respuesta es exitosa
                val articles = response.body()?.articles?.map { it.toDomainModel() } ?: emptyList()
                // Filtramos artículos inválidos
                Result.success(articles.filter { !it.title.contains("[Removed]") })
            } else {
                // Manejo errores HTTP (ej. 401, 404, 500)
                val errorMessage = "Error en la API: ${response.code()}"
                Log.e(TAG, errorMessage)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            // Manejo errores de red/serialización
            val errorMessage = "Error de red/conexión: ${e.message}"
            Log.e(TAG, errorMessage, e)
            Result.failure(e)
        }
    }
}

// Se usa un patrón Factory simple para el repositorio
object RepositoryFactory {
    val newsRepository: NewsRepository by lazy {
        NewsRepository(RetrofitClient.apiService)
    }
}