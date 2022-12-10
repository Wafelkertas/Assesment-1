package com.shidqi.movieassestment.pages.genre

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shidqi.movieassestment.models.movieGenre.Genre
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shidqi.movieassestment.MainViewModel
import com.shidqi.movieassestment.models.movieDiscover.ResultDiscover
import com.shidqi.movieassestment.models.movieTrending.ResultTrending
import com.shidqi.movieassestment.others.IMAGE_URL
import com.shidqi.movieassestment.others.OnBottomReached
import com.shidqi.movieassestment.others.Routes
import com.shidqi.movieassestment.others.conditional
import com.shidqi.movieassestment.pages.movieDetail.YoutubeCompose
import com.valentinilk.shimmer.shimmer
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.SnapOffsets
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

@Composable
fun HomeScreen(mainViewModel: MainViewModel, navController: NavController) {
    val state = mainViewModel.stateOfListCategory.value
    val movieDiscover = mainViewModel.movieDiscover.value
    val movieTrending = mainViewModel.movieTrending.value
    val networkError by remember { mainViewModel.networkError }
    val categoryLoading by remember {
        mainViewModel.categoryLoading
    }

    if (!networkError) {
        MovieList(
            movieGenre = state,
            onClick = { genre ->
                mainViewModel.selectedGenre = genre
                mainViewModel.appBarTitle.value = genre.name
                mainViewModel.genreId = genre.id
                navController.navigate(Routes.MovieList.screen_route)
            },
            isLoading = categoryLoading,
            movieDiscover = movieDiscover,
            movieTrending = movieTrending,
            mainViewModel = mainViewModel,
            navController = navController
        )
    } else {
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


}

@Composable
fun MovieList(
    mainViewModel: MainViewModel,
    movieDiscover: List<ResultDiscover>,
    movieTrending: List<ResultTrending>,
    movieGenre: List<Genre>,
    onClick: (data: Genre) -> Unit,
    isLoading: Boolean,
    navController: NavController
) {
    val trendingLoading by lazy { mainViewModel.movieTrendingLoading.value }
    val discoverLoading by lazy { mainViewModel.movieDiscoverLoading.value }
    val discoverErrorMessage by lazy { mainViewModel.movieDiscoverError.value }
    val trendingErrorMessage by lazy { mainViewModel.movieTrendingError.value }
    val discoverLazyState = rememberLazyListState()
    val trendingLazyState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()

    ) {
        item {
            if (discoverLoading) {
                Card(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .height(300.dp)
                        .shimmer()
                ) {

                }
            }
            LazyRow(
                state = discoverLazyState
            ) {

                items(movieDiscover) { resultDiscover ->
                    Card(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .height(300.dp)

                    ) {

                        Box(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                            ) {
                                YoutubeCompose(
                                    youtubeUrl = mainViewModel.findTrailerVideo(
                                        resultDiscover.movieVideoResponse?.results ?: listOf()
                                    )?.key ?: "", false
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomStart)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data("${IMAGE_URL}${resultDiscover.poster_path}")
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .wrapContentSize()

                                )
                                Column(
                                    modifier = Modifier
                                        .fillParentMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = resultDiscover.original_title,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = mainViewModel.findTrailerVideo(
                                            resultDiscover.movieVideoResponse?.results ?: listOf()
                                        )?.name ?: ""
                                    )
                                }
                            }


                        }
                    }
                }
            }
        }
        item {
            Text(
                text = "Search Movie By Genre",
                modifier = Modifier.padding(8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Yellow,
                fontFamily = FontFamily.Monospace
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (isLoading && movieGenre.isEmpty()) {
                    items(30) {
                        ColumnItemLoading()
                    }
                }

                items(movieGenre) { genre ->
                    ColumnItem(onClick = {
                        onClick(genre)
                    }, data = genre, isLoading = isLoading)
                }

            }
        }


        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Featured this week",
                modifier = Modifier.padding(8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Yellow,
                fontFamily = FontFamily.Monospace
            )

        }
        item {
            LazyRow(Modifier.height(180.dp), state = trendingLazyState) {
                if (trendingLoading) {
                    items(6) {
                        Card(
                            modifier = Modifier
                                .width(120.dp)
                                .fillParentMaxHeight()
                                .shimmer()
                        ) {

                        }
                    }
                }
                if (!trendingLoading) {
                    items(movieTrending) { movieTrendingItem ->
                        Card(
                            modifier = Modifier
                                .width(120.dp)
                                .fillParentMaxHeight()
                                .clickable {
                                    mainViewModel.movieId = movieTrendingItem.id
                                    mainViewModel.selectedTab = 3
                                    navController.navigate(Routes.MovieDetail.screen_route)
                                }
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("${IMAGE_URL}${movieTrendingItem.poster_path}")
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .fillMaxSize(0.9f)

                            )
                        }
                    }
                    item {
                        if (trendingErrorMessage.isNotEmpty()) {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Text(text = trendingErrorMessage)
                            }
                        }
                        trendingLazyState.OnBottomReached {
                            if (mainViewModel.trendingPage != 1) {
                                mainViewModel.getTrendingMovies()
                            }
                        }
                    }

                }

            }

        }


    }
}

@Composable
fun ColumnItem(onClick: (data: Genre) -> Unit, data: Genre, isLoading: Boolean) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .height(40.dp)
            .conditional(!isLoading) {
                clickable { onClick.invoke(data) }
            },
        elevation = 10.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color(0xFF424242),
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = data.name,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun ColumnItemLoading() {
    Card(
        modifier = Modifier
            .width(80.dp)
            .height(40.dp)
            .shimmer(),

        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color(0xFF424242),
        elevation = 10.dp
    ) {

    }
}

@Composable
fun ColumnHeaderLoading() {
    Card(
        modifier = Modifier
            .width(80.dp)
            .height(40.dp)
            .shimmer(),

        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color(0xFF424242),
        elevation = 10.dp
    ) {

    }
}


@Composable
fun StyledTextField() {
    var value by remember { mutableStateOf("") }

    OutlinedTextField(
        value = value, colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = Color(0x66FEFEFE),
        ),
        onValueChange = { value = it },
        label = { Text("Enter text") },
        maxLines = 1,
        textStyle = TextStyle(color = Color.Blue, fontWeight = FontWeight.Bold),
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(30.dp)
    )
}