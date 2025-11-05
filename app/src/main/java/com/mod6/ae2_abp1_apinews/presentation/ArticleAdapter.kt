package com.mod6.ae2_abp1_apinews.presentation

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent // Importación necesaria para Custom Tabs
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mod6.ae2_abp1_apinews.R
import com.mod6.ae2_abp1_apinews.data.model.Article

// Adaptador para mostrar la lista de noticias en un RecyclerView
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

        // 1. Asignación de Texto
        holder.titleTextView.text = article.title
        holder.descriptionTextView.text = article.description ?: "Toca para leer la noticia completa."

        // 2. Mostrar el nombre de la fuente
        holder.sourceTextView.text = "Fuente: ${article.sourceName}"

        // 3. Cargar Imagen con Glide
        val imageUrl = article.urlToImage
        if (imageUrl != null && imageUrl.isNotBlank()) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(holder.thumbnailImageView)
        } else {
            holder.thumbnailImageView.setImageResource(R.drawable.placeholder_image)
        }

        // 4. Implementar clic para abrir el enlace
        // CORRECCIÓN: Se utiliza la aserción de no-nulidad (!!) para resolver el error de compilación.
        if (article.url!!.isNotBlank()) {
            holder.itemView.setOnClickListener {
                try {
                    // 4a. Construir CustomTabsIntent (pestaña ligera de Chrome)
                    val customTabsIntent = CustomTabsIntent.Builder().build()

                    // 4b. Abrir la URL con Custom Tabs.
                    customTabsIntent.launchUrl(holder.itemView.context, Uri.parse(article.url))

                } catch (e: Exception) {
                    // Fallback para dispositivos que no soportan Custom Tabs o Chrome
                    try {
                        // El Intent debe ser creado con la URL asegurada como no nula
                        val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))

                        // OBLIGATORIO: Añadir FLAG_ACTIVITY_NEW_TASK para Intent desde un Context de ViewHolder
                        fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                        if (fallbackIntent.resolveActivity(holder.itemView.context.packageManager) != null) {
                            holder.itemView.context.startActivity(fallbackIntent)
                        } else {
                            // En un entorno de producción, aquí se mostraría un mensaje de error al usuario.
                            e.printStackTrace()
                        }
                    } catch (fallbackException: Exception) {
                        fallbackException.printStackTrace()
                    }
                }
            }
        } else {
            // Si la URL es inválida o nula, aseguramos que no haya un listener activo.
            holder.itemView.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int = articles.size

    fun updateArticles(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged()
    }
}