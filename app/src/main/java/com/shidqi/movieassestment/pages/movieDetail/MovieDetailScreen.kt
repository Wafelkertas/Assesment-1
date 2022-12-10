package com.shidqi.movieassestment.pages.movieDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shidqi.movieassestment.MainViewModel
import com.shidqi.movieassestment.others.IMAGE_URL
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.shidqi.movieassestment.databinding.YoutubeBinding
import com.shidqi.movieassestment.models.movieReviews.MovieReviewResult
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MovieDetailScreen(mainViewModel: MainViewModel) {


    val state by remember {
        mainViewModel.movieDetail
    }
    val isLoading by remember {
        mainViewModel.detailLoading
    }

    val youtubeDetail by remember {
        mainViewModel.movieVideo
    }
    val reviewDetail by remember {
        mainViewModel.movieReview
    }
    val errorMessage by remember{
        mainViewModel.movieDetailError
    }
    LaunchedEffect(key1 = Unit) {
        mainViewModel.getMovieDetail()

    }
    val hours = state?.runtime?.div(60)
    val minutes = state?.runtime?.rem(60)
    if (!isLoading) {

        LazyColumn(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .background(Color(0x66434343))
                .padding(top = 8.dp)
                .fillMaxHeight()
        ) {

            if (errorMessage.isEmpty()){
                item {
                    Text(
                        text = state?.title ?: "",
                        modifier = Modifier.padding(7.dp),
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Light, lineHeight = 35.sp
                    )
                    Text(
                        text = if (state == null) {
                            ""
                        } else "${state?.release_date} ${hours}h ${minutes}m",
                        modifier = Modifier.padding(7.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light, lineHeight = 35.sp, color = Color(0x66FEFEFE)
                    )
                    YoutubeCompose(mainViewModel.findTrailerVideo(youtubeDetail?.results ?: listOf())?.key ?: "", false)

                    Row(
                        modifier = Modifier
                            .height(250.dp)
                            .fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("$IMAGE_URL${state?.poster_path}")
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .width(150.dp)
                                .wrapContentHeight()
                        )
                        Column() {
                            LazyRow() {
                                items(state?.genres ?: listOf()) { genre ->
                                    Chip(
                                        onClick = { /*TODO*/ },
                                        enabled = false,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                                    ) {
                                        Text(text = genre.name)
                                    }
                                }
                            }
                            Text(text = state?.overview ?: "", overflow = TextOverflow.Ellipsis)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(modifier = Modifier.background(Color.Black)) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Reviews", modifier = Modifier
                            .padding(start = 4.dp)
                            .fillMaxWidth())
                        Spacer(modifier = Modifier.height(5.dp))
                        LazyRow() {
                            items(reviewDetail?.results ?: listOf()) { items ->
                                ReviewItem(data = items)
                            }
                            item {
                                reviewDetail?.let {
                                    if (reviewDetail!!.results.isEmpty()){
                                        Text(text = "There is No Reviews Yet", modifier = Modifier.padding(16.dp))
                                    }
                                }

                            }
                        }
                        Spacer(modifier = Modifier.height(50.dp))
                    }
                }
            }


        }
    }
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    if (errorMessage.isNotEmpty()){
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
                            Text(text = errorMessage)
                        }
                    }
                }
            )
        }

    }


}

@Composable
fun ReviewItem(data: MovieReviewResult) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .height(150.dp)
            .padding(start = 4.dp, end = 4.dp)
            .width(240.dp),
        backgroundColor = Color(0xFF434343)
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Text(text = data.author, fontWeight = FontWeight.W700)
            Text(
                text = data.content,
                maxLines = 5,
                fontWeight = FontWeight.Light,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Rating: ${data.author_details.rating}",
                fontSize = 12.sp,
                color = Color(0xFFD3A945)
            )
        }
    }
}

/*
* Composable for youtube view because youtube api doesn't have jetpack compose component
* */
@Composable
fun YoutubeCompose(youtubeUrl: String, autoPlay : Boolean, ) {

    AndroidViewBinding(YoutubeBinding::inflate) {
        val youTubePlayerView: YouTubePlayerView = this.youtubePlayerView
        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                if (autoPlay){
                    youTubePlayer.loadVideo(youtubeUrl, 0f)
                }else{
                    youTubePlayer.cueVideo(youtubeUrl, 0f)
                }
            }

        })
    }
}





