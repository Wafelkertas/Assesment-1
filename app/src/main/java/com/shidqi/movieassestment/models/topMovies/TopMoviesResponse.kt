package com.shidqi.movieassestment.models.topMovies

data class TopMoviesResponse(
    val page: Int,
    val results: List<TopMoviesResult>,
    val total_pages: Int,
    val total_results: Int
)