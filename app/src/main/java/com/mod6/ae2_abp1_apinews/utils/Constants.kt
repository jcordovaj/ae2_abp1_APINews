package com.mod6.ae2_abp1_apinews.utils

// Define las constantes globales usadas a través de la app.
object Constants {
    // API
    const val BASE_URL = "https://newsapi.org/v2/"
    // La API Key debe ser tratada como un secreto. Para fines de este ejercicio, la incluimos aquí,
    // pero en producción se recomienda usar BuildConfig o un archivo de propiedades seguro.
    const val API_KEY = "a767dcb9a77443c0a644ba06d2287ffb"
    // Por defecto, noticias de 'US', se puede cambiar a 'mx', 'ar', 'cl', etc.
    const val DEFAULT_COUNTRY   = "us"
    const val DEFAULT_PAGE_SIZE = 10 // Número de artículos a solicitar por defecto

    // Opcionales (para proyectos con Room o Preferencias)
    // const val DATABASE_NAME = "news.db"
    // const val NEWS_APP_PREFERENCES = "news_app_preferences"
}