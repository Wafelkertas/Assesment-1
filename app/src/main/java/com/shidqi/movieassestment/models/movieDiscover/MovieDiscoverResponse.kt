package com.shidqi.movieassestment.models.movieDiscover

import com.shidqi.movieassestment.models.movieVideo.MovieVideoResponse

data class MovieDiscoverResponse(
    val page: Int,
    val results: List<ResultDiscover>,
    val total_pages: Int,
    val total_results: Int
)

data class ResultDiscover(
    val adult: Boolean,
    val backdrop_path: String,
    val genre_ids: List<Int>,
    val id: Int,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Double,
    val poster_path: String,
    val release_date: String,
    val title: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int,
    var movieVideoResponse: MovieVideoResponse? =null
)