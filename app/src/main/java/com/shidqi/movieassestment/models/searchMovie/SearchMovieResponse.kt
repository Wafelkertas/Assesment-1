package com.shidqi.movieassestment.models.searchMovie

data class SearchMovieResponse(
    val page: Int,
    val results: List<SearchMovieResult>,
    val total_pages: Int,
    val total_results: Int
)