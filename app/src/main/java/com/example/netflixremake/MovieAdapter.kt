package com.example.netflixremake

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.example.netflixremake.model.Movie
import com.squareup.picasso.Picasso

// DEFININDO QUE A CLASSE ADAPTER RECEBE COMO PARAMETRO UMA LISTA DE FILMES
class MovieAdapter(
    private var movies: List<Movie>,
    @LayoutRes private val layoutId: Int,
    private val onItemClickListener: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    // DEFININDO QUAL LAYOUT XML SERA INFLADO NO ADAPTER
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return MovieViewHolder(view)
    }

    // QUANTIDADE DE ITENS EXIBIDOS NO ADAPTER, É O TAMANHO DA LISTA PASSADA COMO ARGUMENTO
    override fun getItemCount(): Int {
        return movies.size
    }

    // DEVOLVE CADA FILME DA LISTA PARA SER EXIBIDO NA TELA.
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(movie: Movie) {
            // ImageCover recebe a referencia da ImageView do XML que será exibido na recyclerView
            val imageCover: ImageView = itemView.findViewById(R.id.image_cover)
            imageCover.setOnClickListener {
                onItemClickListener?.invoke(movie.id)
            }
            Picasso.get().load(movie.coverUrl).placeholder(R.drawable.placeholder_bg)
                .into(imageCover)
        }
    }

    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }
}