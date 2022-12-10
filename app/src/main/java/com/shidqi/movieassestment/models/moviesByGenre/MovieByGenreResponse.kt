package com.shidqi.movieassestment.models.moviesByGenre

data class MovieByGenreResponse(
    val page: Int,
    val results: List<MovieByGenreResult>,
    val total_pages: Int,
    val total_results: Int
)