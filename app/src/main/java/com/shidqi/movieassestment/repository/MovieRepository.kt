package com.shidqi.movieassestment.repository

import com.shidqi.movieassestment.models.movieDetail.MovieDetailResponse
import com.shidqi.movieassestment.models.movieDiscover.MovieDiscoverResponse
import com.shidqi.movieassestment.models.movieGenre.MovieGenreResponse
import com.shidqi.movieassestment.models.movieReviews.MovieReviewResponse
import com.shidqi.movieassestment.models.movieTrending.MovieTrendingResponse
import com.shidqi.movieassestment.models.movieVideo.MovieVideoResponse
import com.shidqi.movieassestment.models.moviesByGenre.MovieByGenreResponse
import com.shidqi.movieassestment.models.searchMovie.SearchMovieResponse
import com.shidqi.movieassestment.models.topMovies.TopMoviesResponse
import com.shidqi.movieassestment.service.IRetrofit

class MovieRepository(private val retrofit : IRetrofit)  {

    suspend fun getTopMovies(page: Int) : SearchMovieResponse{
        return retrofit.getTopMovies(page = page)
    }

    suspend fun searchMovie(query : String, page: Int) : SearchMovieResponse{
        return retrofit.searchMovie(query = query, page = page)
    }

    suspend fun getMovieCategory() : MovieGenreResponse{
        return retrofit.getMovieCategories()
    }

    suspend fun getMovieByGenre(genreId:Int, page : Int) : MovieByGenreResponse{
        return retrofit.getMovieByGenre(genreId = genreId, page = page)
    }

    suspend fun getMovieById(movieId:Int) : MovieDetailResponse{
        return retrofit.getMovieById(movieId)
    }

    suspend fun getMovieVideos(movieId: Int) : MovieVideoResponse{
        return retrofit.getMovieVideo(movieId)
    }

    suspend fun getMovieReview(movieId: Int) : MovieReviewResponse{
        return retrofit.getMovieReviews(movieId)
    }

    suspend fun getMovieDiscover(page: Int) : MovieDiscoverResponse{
        return retrofit.getDiscover(page = page)
    }

    suspend fun getMovieTrending(page: Int) : MovieTrendingResponse{
        return retrofit.getTrending(page = page)
    }
}