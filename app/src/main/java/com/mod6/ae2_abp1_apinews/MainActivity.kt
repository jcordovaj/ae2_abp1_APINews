package com.mod6.ae2_abp1_apinews

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.mod6.ae2_abp1_apinews.data.repository.RepositoryFactory
import com.mod6.ae2_abp1_apinews.presentation.ArticleAdapter
import com.mod6.ae2_abp1_apinews.presentation.NewsViewModel
import com.mod6.ae2_abp1_apinews.presentation.NewsViewModelFactory

// Actúa como vista
class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: ArticleAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa las vistas
        recyclerView  = findViewById(R.id.recycler_view_news)
        progressBar   = findViewById(R.id.progress_bar_loading)
        errorTextView = findViewById(R.id.text_error_message)

        // Inicializa el Adapter y RecyclerView
        newsAdapter = ArticleAdapter(emptyList())
        recyclerView.adapter = newsAdapter

        // Inicializa el ViewModel
        // Usamos la Factory para inyectar la dependencia del Repositorio
        val repository       = RepositoryFactory.newsRepository
        val viewModelFactory = NewsViewModelFactory(repository)
        viewModel            = ViewModelProvider(this,
            viewModelFactory)[NewsViewModel::class.java]

        // Observar LiveData
        observeViewModel()
    }

    private fun observeViewModel() {
        // Observer: Lista de artículos
        viewModel.articles.observe(this) { articles ->
            // Cuando hay nuevos datos, se gatilla el Adapter
            newsAdapter.updateArticles(articles)
            // Si la lista está vacía, muestra mensaje "no hay noticias"
            errorTextView.visibility = if (articles.isEmpty() && viewModel.isLoading.
                value == false) View.VISIBLE else View.GONE
            if (articles.isEmpty() && viewModel.isLoading.value == false) {
                errorTextView.text = "No se encontraron noticias recientes."
            }
        }

        // Observer: Estado de carga
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            // Para evitar clics accidentales, se oculta el "RecyclerView", mientras carga
            recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        // Observer: Mensajes de error
        viewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                errorTextView.text = errorMessage
                errorTextView.visibility = View.VISIBLE
                recyclerView.visibility  = View.GONE
            } else {
                errorTextView.visibility = View.GONE
            }
        }
    }
}