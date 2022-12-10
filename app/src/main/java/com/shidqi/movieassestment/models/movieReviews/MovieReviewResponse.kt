package com.shidqi.movieassestment.models.movieReviews

data class MovieReviewResponse(
    val id: Int,
    val page: Int,
    val results: List<MovieReviewResult>,
    val total_pages: Int,
    val total_results: Int
)