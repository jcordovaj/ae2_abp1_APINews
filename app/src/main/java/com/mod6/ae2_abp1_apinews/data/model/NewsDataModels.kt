package com.mod6.ae2_abp1_apinews.data.model

import com.google.gson.annotations.SerializedName

// Modelo de Dominio
data class Article(
    val title: String,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val sourceName: String
)

// Data Transfer Object (DTO), respuesta JSON de la API.

// DTO: Source (Fuente)
data class SourceDto(
    @SerializedName("name") val name: String?
)
// DTO: Article (Artículo)
data class ArticleDto(
    @SerializedName("source") val source: SourceDto?,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("urlToImage") val urlToImage: String?
) {
    // Función de mapeo para convertir DTO a modelo de Dominio
    fun toDomainModel(): Article {
        return Article(
            title       = title ?: "Noticia sin Título",
            description = description,
            url         = url ?: "",
            urlToImage  = urlToImage,
            sourceName  = source?.name ?: "Fuente Desconocida"
        )
    }
}

// Response de la API
data class NewsResponse(
    @SerializedName("status") val status: String,
    @SerializedName("totalResults") val totalResults: Int,
    @SerializedName("articles") val articles: List<ArticleDto>?
)