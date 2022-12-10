package com.shidqi.movieassestment.pages.movieList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shidqi.movieassestment.MainViewModel
import com.shidqi.movieassestment.models.moviesByGenre.MovieByGenreResult
import com.shidqi.movieassestment.others.IMAGE_URL
import com.shidqi.movieassestment.others.OnBottomReached
import com.shidqi.movieassestment.others.Routes
import com.shidqi.movieassestment.others.conditional
import com.valentinilk.shimmer.shimmer

@Composable
fun MovieListScreen(mainViewModel: MainViewModel, navController: NavController) {
    val state = mainViewModel.stateOfListMovie.value
    val isLoading by remember {
        mainViewModel.categoryLoading
    }
    val errorState by remember { mainViewModel.errorHttp }

    if (errorState != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Card(modifier = Modifier
                .fillMaxSize(0.5f),
                backgroundColor = Color.Red,
                shape = RoundedCornerShape(12.dp),
                content = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(65.dp),
                                tint = Color.White
                            )
                            Text(text = "Error Has Occurred")
                        }
                    }
                }
            )
        }
    }

    LaunchedEffect(key1 = Unit) {

        mainViewModel.getMoviesFromGenre()
    }



    MovieGrid(
        isLoading = isLoading,
        state = state,
        mainViewModel = mainViewModel,
        navController = navController
    )


}

@Composable
fun MovieGrid(
    isLoading: Boolean,
    state: List<MovieByGenreResult>,
    mainViewModel: MainViewModel,
    navController: NavController
) {
    val gridState = rememberLazyGridState()
    LazyVerticalGrid(
        state = gridState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 8.dp),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {


        if (isLoading && state.isEmpty()) {
            items(6) {
                MovieItemLoading()
            }
        }
        items(state) { movie ->
            MovieItem(data = movie, isLoading = isLoading, onClick = {
                mainViewModel.movieId = movie.id
                mainViewModel.appBarTitle.value = movie.title
                navController.navigate(Routes.MovieDetail.screen_route)
            })
        }


    }
    if (isLoading && state.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
    gridState.OnBottomReached {
        if (mainViewModel.page != 1) {
            mainViewModel.getMoviesFromGenre()
        }
    }
}

@Composable
fun MovieItem(data: MovieByGenreResult, isLoading: Boolean, onClick: (MovieByGenreResult) -> Unit) {
    Card(
        modifier = Modifier
            .conditional(!isLoading) {
                clickable { onClick.invoke(data) }
            },
        elevation = 10.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color(0xFF424242),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("$IMAGE_URL${data.poster_path}")
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .fillMaxWidth()
            )
            Text(text = data.title, modifier = Modifier.padding(7.dp))

        }
    }
}

@Composable
fun MovieItemLoading() {
    Card(
        modifier = Modifier
            .height(230.dp)
            .width(120.dp)
            .shimmer(),
        elevation = 10.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color(0xFF424242),
    ) {

    }
}