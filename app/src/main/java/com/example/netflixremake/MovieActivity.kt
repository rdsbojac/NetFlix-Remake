package com.example.netflixremake

import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.netflixremake.databinding.ActivityMovieBinding
import com.example.netflixremake.model.Category
import com.example.netflixremake.model.Movie
import com.example.netflixremake.util.MovieTask
import com.squareup.picasso.Picasso

class MovieActivity : AppCompatActivity(),
    MovieTask.Callback {

    private lateinit var binding: ActivityMovieBinding
    private lateinit var categories: List<Category>
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var movies: List<Movie>

    // Referências a itens do XML
    private lateinit var movieTxtTitle: TextView
    private lateinit var movieTxtDesc: TextView
    private lateinit var movieTxtCast: TextView
    private lateinit var movieImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupWindow()
        setupToolbar()
        blackStatusBar()

        // Referências a itens do XML
        movieTxtTitle = binding.movieTextTitle
        movieTxtDesc = binding.movieTextDescription
        movieTxtCast = binding.movieTextCast
        movieImage = binding.movieImg

        // Lista vazia de filmes
        movies = mutableListOf()

        // RecyclerView e Adapter
        val rvSimilar = binding.movieRvSimilar
        rvSimilar.layoutManager = GridLayoutManager(this, 3)
        movieAdapter =
            MovieAdapter(movies, R.layout.movie_item_similar) { movieId -> onMovieClick(movieId) }
        rvSimilar.adapter = movieAdapter

        val id =
            intent?.getIntExtra("id", 0) ?: throw IllegalStateException("ID não foi encontrado!")

        MovieTask(this).execute("https://meu-api-catalogo.onrender.com/catalogo")
    }

    override fun onPreExecute() {
        // Implementação opcional
    }

    override fun onResult(categories: List<Category>) {
        this.categories = categories

        // Pegue o ID do filme selecionado
        val id = intent?.getIntExtra("id", 0) ?: return

        // Encontre o filme selecionado e a categoria correspondente
        val selectedMovie = categories.flatMap { it.movies }.find { it.id == id }

        // Atualize a UI com os detalhes do filme
        selectedMovie?.let { movieDetail ->
            movieTxtTitle.text = movieDetail.title
            movieTxtDesc.text = movieDetail.description
            movieTxtCast.text = getString(R.string.cast, movieDetail.cast.joinToString(", "))
            Picasso.get().load(movieDetail.coverUrl).placeholder(R.drawable.placeholder_bg)
                .into(movieImage)

            // Filtrar os filmes da mesma categoria
            val sameCategoryMovies =
                categories.find { it.movies.contains(movieDetail) }?.movies?.filter { it.id != movieDetail.id }
            movieAdapter.updateMovies(sameCategoryMovies ?: emptyList())
        }
    }

    private fun onMovieClick(movieId: Int) {
        // Encontre o filme selecionado na lista de filmes
        val selectedMovie = categories.flatMap { it.movies }.find { it.id == movieId }
        selectedMovie?.let { movieDetail ->
            // Atualizar a UI com os detalhes do filme clicado
            updateMovieDetails(movieDetail)

            // Filtrar os filmes da mesma categoria
            val sameCategoryMovies =
                categories.find { it.movies.contains(movieDetail) }?.movies?.filter { it.id != movieDetail.id }
            movieAdapter.updateMovies(sameCategoryMovies ?: emptyList())
        }
    }

    private fun updateMovieDetails(movie: Movie) {
        movieTxtTitle.text = movie.title
        movieTxtDesc.text = movie.description
        movieTxtCast.text = getString(R.string.cast, movie.cast.joinToString(", "))
        Picasso.get().load(movie.coverUrl).placeholder(R.drawable.placeholder_bg).into(movieImage)
    }


    override fun onFailure(message: String) {
        Log.e("MovieActivity", message)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupWindow() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.movie)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun blackStatusBar() {
        // Pintando de preto a status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.textColorPrimary)
        // Configura o contraste do texto (claro ou escuro)
        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars =
            false // Texto claro
    }

    private fun setupToolbar() {
        // Toolbar da tela de informações do filme
        val toolbar = binding.toolbarMovie
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null
    }
}
