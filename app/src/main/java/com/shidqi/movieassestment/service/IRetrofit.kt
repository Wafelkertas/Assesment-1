package com.shidqi.movieassestment.service

import com.shidqi.movieassestment.models.movieDetail.MovieDetailResponse
import com.shidqi.movieassestment.models.movieDiscover.MovieDiscoverResponse
import com.shidqi.movieassestment.models.movieGenre.MovieGenreResponse
import com.shidqi.movieassestment.models.movieReviews.MovieReviewResponse
import com.shidqi.movieassestment.models.movieTrending.MovieTrendingResponse
import com.shidqi.movieassestment.models.movieVideo.MovieVideoResponse
import com.shidqi.movieassestment.models.moviesByGenre.MovieByGenreResponse
import com.shidqi.movieassestment.models.searchMovie.SearchMovieResponse
import com.shidqi.movieassestment.models.topMovies.TopMoviesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IRetrofit {

    /**
     * API to get Top Rated Movies
     * **/
    @GET("movie/top_rated")
    suspend fun getTopMovies(@Query("page") page:Int
    ) : SearchMovieResponse

    /**
     * API to get Movie Categories
     * **/
    @GET("genre/movie/list")
    suspend fun getMovieCategories(
    ) : MovieGenreResponse

    /**
     * API to get Movie by Genre
     * **/
    @GET("discover/movie")
    suspend fun getMovieByGenre(@Query("with_genres") genreId:Int, @Query("page") page:Int) : MovieByGenreResponse

    /**
     * API to get movie detail
     * **/
    @GET("movie/{id}")
    suspend fun getMovieById(@Path("id") movieId:Int) :MovieDetailResponse

    /**
     * API to get movie videos
     * **/
    @GET("movie/{id}/videos")
    suspend fun getMovieVideo(@Path("id") movieId:Int) :MovieVideoResponse

    /**
     * API to get movie reviews
     * **/
    @GET("movie/{id}/reviews")
    suspend fun getMovieReviews(@Path("id") movieId:Int) : MovieReviewResponse

    /**
     * API to search movie by query
     * **/
    @GET("search/movie")
    suspend fun searchMovie(@Query("query") query : String, @Query("page") page: Int) : SearchMovieResponse

    /**
     * API to search discover movie
     * **/
    @GET("discover/movie")
    suspend fun getDiscover(@Query("page") page: Int) : MovieDiscoverResponse


    @GET("trending/{mediaType}/{typeWindow}")
    suspend fun getTrending(
        @Path("mediaType") mediaType: String = "all",
        @Path("typeWindow") typeWindow: String = "week",
        @Query("page") page: Int,
    ): MovieTrendingResponse
}