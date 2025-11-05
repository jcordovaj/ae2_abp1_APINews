package com.mod6.ae2_abp1_apinews.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mod6.ae2_abp1_apinews.data.model.Article
import com.mod6.ae2_abp1_apinews.data.repository.NewsRepository
import kotlinx.coroutines.launch

// ViewModel maneja la lógica de la UI y expone los datos via LiveData
class NewsViewModel(private val repository: NewsRepository) : ViewModel() {

    // LiveData que expone la lista de noticias a la Activity (UI)
    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> = _articles

    // LiveData para manejar el estado de carga
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData para manejar mensajes de error
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        // Cargar noticias automáticamente al iniciar
        fetchNews()
    }

    // Función para solicitar las noticias
    fun fetchNews() {
        _isLoading.value = true // Mostrar indicador de carga
        _errorMessage.value = null // Limpiar error previo

        // Usamos viewModelScope para lanzar una coroutine segura para el ciclo de vida
        viewModelScope.launch {
            val result = repository.getTopHeadlines()

            _isLoading.value = false // Ocultar indicador de carga

            result.onSuccess { newArticles ->
                _articles.value = newArticles // Actualizar LiveData con las noticias
            }.onFailure { exception ->
                // Mostrar error si la llamada falla
                _errorMessage.value = "Error al cargar noticias: ${exception.message}"
            }
        }
    }
}

// Factory simple para instanciar el ViewModel con el Repositorio
class NewsViewModelFactory(private val repository: NewsRepository) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}