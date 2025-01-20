package com.example.netflixremake.model

import androidx.annotation.DrawableRes

data class Movie(
    val id: Int,
    val coverUrl: String,
    val title: String = "",
    val description: String = "",
    val cast: List<String>
) {

}
