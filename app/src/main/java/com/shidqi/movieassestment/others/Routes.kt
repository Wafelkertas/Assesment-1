package com.shidqi.movieassestment.others

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Routes(var title:String, var icon: ImageVector, var screen_route:String){

    object SearchMovie : Routes("Search Movie", Icons.Outlined.Search,"search_movie")
    object HomeScreen: Routes("Home",Icons.Outlined.LiveTv,"home")
    object MovieDetail: Routes("Top Movie",Icons.Outlined.Movie,"movie_detail")
    object MovieList: Routes("Top Movie",Icons.Outlined.Movie,"movie_list")

}