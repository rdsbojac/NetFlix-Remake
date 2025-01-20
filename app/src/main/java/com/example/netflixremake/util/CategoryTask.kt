package com.example.netflixremake.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.netflixremake.model.Category
import com.example.netflixremake.model.Movie
import okio.Buffer
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection
import kotlin.math.log

class CategoryTask(private val callback: Callback) {

    private val handler = Handler(Looper.getMainLooper())

    interface Callback {
        fun onPreExecute()
        fun onResult(categories: List<Category>)
        fun onFailure(message: String)
    }

    fun execute(url: String){

        callback.onPreExecute()
        var urlConnection : HttpsURLConnection? = null
        var buffer: BufferedInputStream? = null
        var stream: InputStream? = null

        try{
            // utilizando a UI-THREAD(PRINCIPAL)
            val executor = Executors.newSingleThreadExecutor()

            executor.execute {
                //utilizando a NOVA-THREAD
                val requestUrl = URL(url) // abrir uma Url
                val urlConnection = requestUrl.openConnection() as HttpsURLConnection // abrir a conexao
                urlConnection.connectTimeout = 2000 // tempo de conexao (2s)
                urlConnection.readTimeout = 2000 // tempo de leitura (2s)

                val statusCode: Int = urlConnection.responseCode // status da resposta do servidor

                if (statusCode > 400) {
                    throw IOException("Erro na comunicação com o servidor!")
                }

                val stream = urlConnection.inputStream // sequencia de byteS

                // FORMA DE LEITURA DE UM JSON
                // FORMA 1 -> rapida e simples
                //val jsonAsString = stream.bufferedReader().use{ it.readText()}

                // FORMA 2 ->
                val buffer = BufferedInputStream(stream)
                //toStrig entra em loop infinito e transforma byte a byte o strem em String(json em string)
                val jsonAsString = toString(buffer)

                // o Json está preparado para ser contetido em um DATA CLASS
                val categories = toCategories(jsonAsString)

                Log.i("json",categories.toString())

                //AQUI VOLTA E RODA DENTRO DA UI PRINCIPAL(MAIN)
                handler.post{
                    callback.onResult(categories)
                }

            }

        }catch (e: IOException){
            val message = e.message?: "Erro desconhecido"
            Log.i("teste",message,e)
            handler.post {
                callback.onFailure(message)
            }


        } finally {
            urlConnection?.disconnect()
            stream?.close()
            buffer?.close()

        }
    }

    private fun toCategories(jsonAsString: String) : List<Category> {
        //criando lista vazia de categorias
        val categories = mutableListOf<Category>()

        // transformando o tipo json para string
        val jsonRoot = JSONObject(jsonAsString)
        // pegando a lista de categorias da string formatada atraves do json
        val jsonCategories = jsonRoot.getJSONArray("categories")

        // loop para ler todos os itens dentro de categories que vai do indice 0 até o tamanho da lista de categorias
        for (i in 0 until jsonCategories.length()){
            //pegando cada categoria pelo indice dentro do jsonCategories
            val jsonCategory = jsonCategories.getJSONObject(i)

            // pegando o titulo da categoria pela chave "title"
            val title = jsonCategory.getString("title")
            // pegando a array de filmes que estao dentro de cada categoria pela getJSONArray pela chave "movie"
            val jsonMovies = jsonCategory.getJSONArray("movies")
            // pegando as informações isoladas de cada filme

            val movies = mutableListOf<Movie>()
            for (j in 0 until jsonMovies.length()){
                val jsonMovie = jsonMovies.getJSONObject(j)
                val id = jsonMovie.getInt("id")
                val coverUrl = jsonMovie.getString("cover_url")
                val title = jsonMovie.getString("title")
                val description = jsonMovie.getString("description")
                val jsonCast = jsonMovie.getJSONArray("cast")
                val cast = mutableListOf<String>()
                for (k in 0 until jsonCast.length()) {
                    cast.add(jsonCast.getString(k))
                }
                movies.add(Movie(id,coverUrl,title,description,cast))
            }

            categories.add(Category(title,movies))

        }

        return categories
    }


    // TRANSFORMANDO O STREAM EM STRING USANDO UM LAÇO INFINITO E LENDO BYTE A BYTE
    private fun toString(stream: InputStream) : String{
        val bytes = ByteArray(1024)
        val baos = ByteArrayOutputStream()
        while (true){
            val read = stream.read(bytes)
            if (read <= 0) break
            else{
                baos.write(bytes, 0, read)
            }
        }
        return String(baos.toByteArray())
    }
}