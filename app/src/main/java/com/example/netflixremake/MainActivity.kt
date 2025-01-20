package com.example.netflixremake

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netflixremake.databinding.ActivityMainBinding
import com.example.netflixremake.model.Category
import com.example.netflixremake.model.Movie
import com.example.netflixremake.util.CategoryTask

class MainActivity : AppCompatActivity(), CategoryTask.Callback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var progressBar: ProgressBar
    private var categories = mutableListOf<Category>()
    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupWindow()

        // Pintando o status bar de preto
        window.statusBarColor = ContextCompat.getColor(this, R.color.textColorPrimary)
        // Configura o contraste do texto (claro ou escuro)
        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = false // Texto claro
        Log.i("teste","onCreate")


        //ProgressBar
        progressBar = binding.progressMain

        // LISTA DE FILMES VAZIA
        categories = mutableListOf<Category>()

        // DEFININDO O ADAPTER DA RECYCLER VIEW VERTICAL, QUE RECEBE UMA LISTA DE CATEGORIAS.
        val rv = binding.rvMain
        adapter = CategoryAdapter(categories) { id ->
            val intent = Intent(this@MainActivity, MovieActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter


        CategoryTask(this).execute("https://meu-api-catalogo.onrender.com/catalogo")
    }

    private fun setupWindow(){
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onPreExecute() {
        progressBar.visibility = View.VISIBLE
    }

    override fun onResult(categories: List<Category>) {
        // aqui será quando o categorytask chamara de volta
        // (callback) -> listener
        Toast.makeText(this, "SUCESSO AO CARREGAR DADOS", Toast.LENGTH_SHORT).show()

        this.categories.clear()
        this.categories.addAll(categories)
        adapter.notifyDataSetChanged() // Força o adapter a redesenhar na tela

        progressBar.visibility = View.GONE
    }

    override fun onFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        progressBar.visibility = View.GONE
    }
}

