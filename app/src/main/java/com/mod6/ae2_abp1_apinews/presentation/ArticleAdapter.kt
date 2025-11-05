package com.mod6.ae2_abp1_apinews.presentation

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mod6.ae2_abp1_apinews.R
import com.mod6.ae2_abp1_apinews.data.model.Article
import androidx.core.net.toUri

// Muestra el feed de noticias en un RecyclerView
class ArticleAdapter(private var articles: List<Article>) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView       = view.findViewById(R.id.text_title)
        val descriptionTextView: TextView = view.findViewById(R.id.text_description)
        val sourceTextView: TextView      = view.findViewById(R.id.text_source_name)
        val thumbnailImageView: ImageView = view.findViewById(R.id.image_thumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]

        // Asignación de Texto
        holder.titleTextView.text       = article.title
        holder.descriptionTextView.text = article.description ?: "Toca para leer la noticia completa."

        // Mostrar el nombre de la fuente (ej. "reuters")
        holder.sourceTextView.text = "Fuente: ${article.sourceName}"

        // Cargar la miniatura con Glide, si no hay imagen, muestra el placeholder
        val imageUrl  = article.urlToImage
        if (imageUrl != null && imageUrl.isNotBlank()) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(holder.thumbnailImageView)
        } else {
            holder.thumbnailImageView.setImageResource(R.drawable.placeholder_image)
        }

        // Abrir el enlace de la noticia
        if (article.url!!.isNotBlank()) {
            holder.itemView.setOnClickListener {
                try {
                    // Para optimizar recursos se implementa 'CustomTabsIntent'
                    val customTabsIntent = CustomTabsIntent.Builder().build()
                    customTabsIntent.launchUrl(holder.itemView.context,
                        article.url.toUri())

                } catch (e: Exception) {
                    // Plan B: Fallback para dispositivos que no soportan Custom Tabs o Chrome
                    try {
                        val fallbackIntent = Intent(Intent.ACTION_VIEW,
                            article.url.toUri())
                        fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                        if (fallbackIntent.resolveActivity(holder.
                            itemView.context.packageManager) != null) {
                            holder.itemView.context.startActivity(fallbackIntent)
                        } else {
                            e.printStackTrace()
                        }
                    } catch (fallbackException: Exception) {
                        fallbackException.printStackTrace()
                    }
                }
            }
        } else {
            holder.itemView.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int = articles.size

    fun updateArticles(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged()
    }
}