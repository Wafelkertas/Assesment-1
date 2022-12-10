package com.shidqi.movieassestment.pages.searchMovie

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shidqi.movieassestment.MainViewModel
import com.shidqi.movieassestment.models.searchMovie.SearchMovieResult
import com.shidqi.movieassestment.others.IMAGE_URL
import com.shidqi.movieassestment.others.OnBottomReached
import com.shidqi.movieassestment.others.Routes
import com.valentinilk.shimmer.shimmer

@Composable
fun SearchMovie(mainViewModel: MainViewModel, navController: NavController) {

    val lazyColumnState = rememberLazyListState()
    val textSearch by mainViewModel.textSearch.collectAsState()
    val showClearIcon = rememberSaveable { mutableStateOf(false) }.value
    val data by remember { mainViewModel.searchMovies }
    val isLoading by remember {
        mainViewModel.searchLoading
    }

    val errorState by remember { mainViewModel.errorSearch }

    if (errorState.isNotEmpty()) {
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
                            Text(text = errorState)
                        }
                    }
                }
            )
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
    Column {
        TextField(
            value = textSearch,
            onValueChange = mainViewModel::setSearchText,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    tint = MaterialTheme.colors.onBackground,
                    contentDescription = "Search icon"
                )
            },
            trailingIcon = {
                if (showClearIcon) {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            tint = MaterialTheme.colors.onBackground,
                            contentDescription = "Clear icon"
                        )
                    }
                }
            },
            maxLines = 1,
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
            placeholder = { Text(text = "Search Movie") },

            textStyle = MaterialTheme.typography.subtitle1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colors.background, shape = RectangleShape)
        )
        LazyColumn(
            state = lazyColumnState,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
        ) {

            items(data) { item ->
                SearchMovieItem(item = item, onClick = {
                    mainViewModel.movieId = item.id
                    mainViewModel.appBarTitle.value = item.title
                    navController.navigate(Routes.MovieDetail.screen_route)
                }, parentModifier = Modifier
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                    .fillParentMaxHeight(0.6f)
                    .width(200.dp))
            }
            if (isLoading && mainViewModel.topMoviePage == 1 && mainViewModel.searchMoviePage == 1) {
                items(5) {
                    SearchMovieItemLoading()
                }
            }


        }

        lazyColumnState.OnBottomReached {
            if (mainViewModel.textSearchContainer.isNotEmpty() && mainViewModel.searchMoviePage != 1) {
                mainViewModel.searchMovie(mainViewModel.textSearchContainer)
            }
            if (mainViewModel.textSearchContainer.isEmpty() && mainViewModel.topMoviePage != 1) {
                mainViewModel.getTopMovies()
            }
        }
    }
}

@Composable
private fun SearchMovieItem(item: SearchMovieResult,parentModifier: Modifier, onClick: (data: SearchMovieResult) -> Unit) {

    val year = item.release_date.removeRange(3, 9)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(item)
            },
        backgroundColor = Color(0xFF434343),
        shape = RoundedCornerShape(12.dp),

        ) {
        Row(
            horizontalArrangement = Arrangement.Start
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("$IMAGE_URL${item.poster_path}")
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = parentModifier
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.title,
                    modifier = Modifier.padding(7.dp),
                    fontWeight = FontWeight.W700,
                    fontSize = 18.sp,
                    color = Color(0xFFD3A945)
                )

                Text(
                    text = item.overview,
                    modifier = Modifier
                        .padding(7.dp)
                        .height(150.dp),
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.W600,
                )
                Text(
                    modifier = Modifier.padding(7.dp),
                    text = "Language: ${item.original_language}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = year, modifier = Modifier.padding(7.dp))
            }

        }
    }
}

@Composable
private fun SearchMovieItemLoading() {

    Card(
        modifier = Modifier
            .height(300.dp)
            .fillMaxWidth()
            .shimmer(),
        backgroundColor = Color(0xFF434343),
        shape = RoundedCornerShape(12.dp),

        ) {

    }
}