package com.mod6.ae2_abp1_apinews

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.mod6.ae2_abp1_apinews.R
import com.mod6.ae2_abp1_apinews.data.repository.RepositoryFactory
import com.mod6.ae2_abp1_apinews.presentation.ArticleAdapter
import com.mod6.ae2_abp1_apinews.presentation.NewsViewModel
import com.mod6.ae2_abp1_apinews.presentation.NewsViewModelFactory

// Actividad principal que actúa como la capa de vista
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: ArticleAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Asegúrate de que este layout coincida con el nombre del archivo XML: activity_main.xml
        setContentView(R.layout.activity_main)

        // 1. Inicialización de Vistas
        recyclerView = findViewById(R.id.recycler_view_news)
        progressBar = findViewById(R.id.progress_bar_loading)
        errorTextView = findViewById(R.id.text_error_message)

        // 2. Inicialización del Adapter y RecyclerView
        newsAdapter = ArticleAdapter(emptyList())
        recyclerView.adapter = newsAdapter

        // 3. Inicialización del ViewModel
        // Usamos la Factory para inyectar la dependencia del Repositorio
        val repository = RepositoryFactory.newsRepository
        val viewModelFactory = NewsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[NewsViewModel::class.java]

        // 4. Observar LiveData (Actualización en tiempo real)
        observeViewModel()
    }

    private fun observeViewModel() {
        // Observar la lista de artículos
        viewModel.articles.observe(this) { articles ->
            // Cuando hay nuevos datos, se actualiza el Adapter automáticamente
            newsAdapter.updateArticles(articles)
            // Mostrar mensaje de "no hay noticias" si la lista está vacía
            errorTextView.visibility = if (articles.isEmpty() && viewModel.isLoading.value == false) View.VISIBLE else View.GONE
            if (articles.isEmpty() && viewModel.isLoading.value == false) {
                errorTextView.text = "No se encontraron noticias recientes."
            }
        }

        // Observar el estado de carga
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            // Ocultar el RecyclerView si está cargando para evitar clics accidentales
            recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        // Observar mensajes de error
        viewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                errorTextView.text = errorMessage
                errorTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                errorTextView.visibility = View.GONE
            }
        }
    }
}