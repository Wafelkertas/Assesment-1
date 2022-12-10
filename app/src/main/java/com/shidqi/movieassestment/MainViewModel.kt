package com.shidqi.movieassestment

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shidqi.movieassestment.models.movieDetail.MovieDetailResponse
import com.shidqi.movieassestment.models.movieDiscover.ResultDiscover
import com.shidqi.movieassestment.models.movieGenre.Genre
import com.shidqi.movieassestment.models.movieReviews.MovieReviewResponse
import com.shidqi.movieassestment.models.movieTrending.ResultTrending
import com.shidqi.movieassestment.models.movieVideo.MovieVideoResponse
import com.shidqi.movieassestment.models.movieVideo.MovieVideoResult
import com.shidqi.movieassestment.models.moviesByGenre.MovieByGenreResult
import com.shidqi.movieassestment.models.searchMovie.SearchMovieResult
import com.shidqi.movieassestment.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val movieRepository: MovieRepository) :
    ViewModel() {


    private val _textSearch = MutableStateFlow("")
    val textSearch: StateFlow<String> = _textSearch.asStateFlow()
    val categoryLoading = mutableStateOf<Boolean>(true)
    val errorHttp = mutableStateOf<HttpException?>(null)
    val errorSearch = mutableStateOf<String>("")
    val stateOfListCategory: MutableState<List<Genre>> = mutableStateOf(listOf())
    val stateOfListMovie: MutableState<List<MovieByGenreResult>> = mutableStateOf(listOf())

    val movieTrending: MutableState<List<ResultTrending>> = mutableStateOf(listOf())
    val movieTrendingError: MutableState<String> = mutableStateOf("")
    val movieTrendingLoading: MutableState<Boolean> = mutableStateOf(false)


    val movieDiscover: MutableState<List<ResultDiscover>> = mutableStateOf(listOf())
    val movieDiscoverError: MutableState<String> = mutableStateOf("")
    val movieDiscoverLoading: MutableState<Boolean> = mutableStateOf(false)

    val movieDetail: MutableState<MovieDetailResponse?> = mutableStateOf(null)
    val movieVideo: MutableState<MovieVideoResponse?> = mutableStateOf(null)
    val movieDetailError : MutableState<String> = mutableStateOf("")
    val detailLoading = mutableStateOf<Boolean>(true)

    val searchMovies: MutableState<List<SearchMovieResult>> = mutableStateOf(listOf())
    val searchLoading = mutableStateOf<Boolean>(true)

    val movieReview: MutableState<MovieReviewResponse?> = mutableStateOf(null)
    val networkError: MutableState<Boolean> = mutableStateOf(false)

    val appBarTitle = mutableStateOf("")

    var textSearchContainer: String = ""
    var selectedTab = 0
    var movieId: Int = 0
    var genreId: Int = 0
    var selectedGenre: Genre? = null
    var page: Int = 1
    var trendingPage: Int = 1
    var topMoviePage: Int = 1
    var searchMoviePage: Int = 1

    init {
        getMovieCategories()
        getTopMovies()
        getDiscoverMovies()
        getTrendingMovies()
        viewModelScope.launch {
            // As soon the textSearch flow changes,
            // if the user stops typing for 1000ms, the item will be emitted
            textSearch.debounce(1000).collect { query ->
                // Call the search function here using the query param
                if (query.isNotEmpty()) {
                    searchMovies.value = listOf()
                    textSearchContainer = query
                    searchMovie(query = query)
                }
                if (query.isEmpty()) {
                    topMoviePage = 1
                    getTopMovies()
                }
            }
        }
    }

    fun getMovieCategories() {
        viewModelScope.launch {
            errorHttp.value = null
            categoryLoading.value = true
            delay(2000)

            try {
                val response = movieRepository.getMovieCategory()

                categoryLoading.value = false
                stateOfListCategory.value = response.genres
            } catch (e: HttpException) {
                categoryLoading.value = false
                errorHttp.value = e
                stateOfListCategory.value = listOf()
            } catch (e: IOException) {
                networkError.value = true
            }
        }
    }

    fun getMoviesFromGenre() {
        viewModelScope.launch {
            errorHttp.value = null
            categoryLoading.value = true

            delay(2000)
            try {

                val response = movieRepository.getMovieByGenre(genreId, page)
                categoryLoading.value = false
                stateOfListMovie.value += response.results
                page += 1
            } catch (e: HttpException) {
                categoryLoading.value = false
                errorHttp.value = e
                stateOfListMovie.value = listOf()
            } catch (e: IOException) {
                networkError.value = true

            }
        }
    }

    fun findTrailerVideo(data: List<MovieVideoResult>): MovieVideoResult? {
        val movieVideo = data.find {
            it.type == "Trailer"
        }
        return movieVideo
    }


    fun getMovieDetail() {
        viewModelScope.launch {
            detailLoading.value = true
            delay(2000)
            try {
                val detailResponse = movieRepository.getMovieById(movieId = movieId)
                val videoResponse = movieRepository.getMovieVideos(movieId = movieId)
                val reviewResponse = movieRepository.getMovieReview(movieId = movieId)

                movieReview.value = reviewResponse
                movieDetail.value = detailResponse
                movieVideo.value = videoResponse
                detailLoading.value = false
            } catch (e: HttpException) {
                detailLoading.value = false
                errorHttp.value = e
                movieDetailError.value = e.message()
                movieDetail.value = null
            } catch (e: IOException) {
                detailLoading.value = false
                networkError.value = true
                movieDetailError.value = "Network Error"
            }
        }
    }

    fun setSearchText(it: String) {
        _textSearch.value = it
    }

    fun searchMovie(query: String) {

        viewModelScope.launch {
            errorSearch.value = ""
            searchLoading.value = true
            delay(2000)

            try {

                val response = movieRepository.searchMovie(query = query, searchMoviePage)
                searchLoading.value = false
                if (searchMoviePage > 1 && response.results.isNotEmpty()) {
                    searchMovies.value += response.results
                    searchMoviePage += 1
                }
                if (searchMoviePage == 1 && response.results.isNotEmpty()) {
                    searchMovies.value = response.results
                    searchMoviePage = 1
                }

            } catch (e: HttpException) {
                searchMoviePage = 1
                searchLoading.value = false
                errorSearch.value = e.message()

            } catch (e: IOException) {
                searchLoading.value = false
                errorSearch.value = "Network Error"
                networkError.value = true

            }
        }
    }

    fun getTopMovies() {
        viewModelScope.launch {
            categoryLoading.value = true
            try {
                val response = movieRepository.getTopMovies(page = topMoviePage)
                categoryLoading.value = false
                if (topMoviePage > 1 && response.results.isNotEmpty()) {

                    searchMovies.value += response.results
                }
                if (topMoviePage == 1 && response.results.isNotEmpty()) {
                    searchMovies.value = response.results

                }

                topMoviePage += 1

            } catch (e: HttpException) {
                topMoviePage = 1
                categoryLoading.value = false

            } catch (e: IOException) {
                networkError.value = true
            }
        }
    }

    private fun getDiscoverMovies() {
        movieDiscoverError.value = ""
        movieDiscoverLoading.value = true
        viewModelScope.launch {
            try {
                val data = movieRepository.getMovieDiscover(page = 1)
                data.results.map { eachItem ->
                    val videoResponse = movieRepository.getMovieVideos(movieId = eachItem.id)
                    eachItem.movieVideoResponse = videoResponse
                }
                movieDiscover.value = data.results
                movieDiscoverLoading.value = false

            } catch (e: HttpException) {
                movieDiscoverLoading.value = false
                movieDiscoverError.value = e.message()
            } catch (e: IOException) {
                movieDiscoverError.value = "No Network"
                movieDiscoverLoading.value = true
                networkError.value = true
            }
        }
    }

    fun getTrendingMovies() {
        movieTrendingError.value = ""
        movieTrendingLoading.value = true
        viewModelScope.launch {
            try {
                val data = movieRepository.getMovieTrending(page = trendingPage)
                trendingPage += 1
                movieTrending.value += data.results
                movieTrendingLoading.value = false
            } catch (e: HttpException) {
                movieTrending.value = listOf()
                movieTrendingError.value = e.message()
                trendingPage = 1
                movieTrendingLoading.value = false
            } catch (e: IOException) {
                trendingPage = 1
                movieTrending.value = listOf()
                movieTrendingError.value = "Network Error"
                networkError.value = true
                movieTrendingLoading.value = false
            }
        }
    }


}