
package com.example.netflixremake

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netflixremake.model.Category
import com.example.netflixremake.model.Movie

// DEFININDO QUE A CLASSE ADAPTER RECEBE COMO PARAMETRO UMA LISTA HORIZONTAL DE CATEGORIAS
class CategoryAdapter(private val categories: List<Category>,
    private val onItemClickListener: (Int) -> Unit
    ) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    // QUANTIDADE DE ITENS EXIBIDOS NO ADAPTER, É O TAMANHO DA LISTA PASSADA COMO ARGUMENTO
    override fun getItemCount(): Int {
        return categories.size
    }

    // DEVOLVE CADA FILME DA LISTA PARA SER EXIBIDO NA TELA.
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(category: Category){
            // TxtTitle recebe a referencia do TextView do XML que será exibido na recyclerView
            // em cada categoria
            val txtTitle : TextView = itemView.findViewById(R.id.text_title)
            txtTitle.text = category.name

            // rcCategory recebe a referencia a RecyclerView do XML
            val rvCategory: RecyclerView = itemView.findViewById(R.id.rv_category)
            // DENIFININDO O TIPO DE LAYOUT(HORIZONTAL) DA RECYCLERVIEW e UTILIZANDO COMO CONTEXT
            // o "itemView.context"
            rvCategory.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
            // ADAPTER RECEBE INSTANCIA DO MOVIE ADAPTER QUE RECEBE COMO ARGUMENTO A "category.movies"
            rvCategory.adapter = MovieAdapter(category.movies, R.layout.movie_item, onItemClickListener)
        }
    }
}

