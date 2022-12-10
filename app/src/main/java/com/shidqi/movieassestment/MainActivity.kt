package com.shidqi.movieassestment

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shidqi.movieassestment.others.Routes
import com.shidqi.movieassestment.pages.genre.HomeScreen
import com.shidqi.movieassestment.pages.movieDetail.MovieDetailScreen
import com.shidqi.movieassestment.pages.movieList.MovieListScreen
import com.shidqi.movieassestment.pages.searchMovie.SearchMovie
import com.shidqi.movieassestment.ui.theme.MovieAssestmentTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            MovieAssestmentTheme(darkTheme = true) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Main()
                }
            }
        }
    }
}



/**
 * Main Composable function to inflate compose view
 * **/
@Composable
fun Main(
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val topBarState = rememberSaveable { (mutableStateOf(false)) }
    val bottomBarState = rememberSaveable { (mutableStateOf(true)) }

    val errorState by remember { mainViewModel.errorHttp }
    val scaffoldState = rememberScaffoldState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""
    val newTittle by remember { mainViewModel.appBarTitle }

    var backNavigation: String = ""

    when (currentRoute) {
        Routes.HomeScreen.screen_route -> {
            bottomBarState.value = true
            topBarState.value = false
        }
        Routes.SearchMovie.screen_route -> {
            bottomBarState.value = true
            topBarState.value = false
        }
        Routes.MovieDetail.screen_route -> {
            bottomBarState.value = false
            topBarState.value = true
            backNavigation = if (mainViewModel.selectedTab == 0){
                Routes.MovieList.screen_route
            } else if(mainViewModel.selectedTab == 1){
                Routes.SearchMovie.screen_route
            }else{
                Routes.HomeScreen.screen_route
            }
        }
        Routes.MovieList.screen_route -> {
            bottomBarState.value = false
            topBarState.value = true

            backNavigation = Routes.HomeScreen.screen_route

        }
    }
    if (errorState != null) {

        LaunchedEffect(key1 = Unit) {
            scaffoldState.snackbarHostState.showSnackbar(
                message = "An Error Occured, HTTP Error ${errorState!!.code()}",
            )
        }
    }
    Scaffold(scaffoldState = scaffoldState, topBar = {
        AnimatedVisibility(
            visible = topBarState.value,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it })
        ) {
            TopAppBar(navigationIcon = {
                IconButton(
                    onClick = {
                        mainViewModel.appBarTitle.value =  ""
                        mainViewModel.movieVideo.value = null
                        mainViewModel.movieDetail.value = null
                        mainViewModel.stateOfListMovie.value = listOf()
                        mainViewModel.errorHttp.value = null
                        mainViewModel.page = 1
                        navigateTo(navController, backNavigation)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Filled.ChevronLeft,
                        tint = Color.White, modifier = Modifier.size(35.dp),
                        contentDescription = null
                    )
                }
            }, title = { Text(text = newTittle) })
        }
    },
        bottomBar = {
            BottomNavigation(
                navController = navController,
                bottomBarState = bottomBarState,
                currentRoute = currentRoute,
                mainViewModel = mainViewModel
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavigationGraph(navController = navController, mainViewModel)
        }

    }


}

/**
 * Navigation Configuration
 * **/
@Composable
fun NavigationGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    NavHost(navController, startDestination = Routes.HomeScreen.screen_route) {
        composable(Routes.SearchMovie.screen_route) {
            SearchMovie(mainViewModel, navController)
        }
        composable(Routes.HomeScreen.screen_route) {
            HomeScreen(mainViewModel, navController)
        }
        composable(Routes.MovieDetail.screen_route) {
            MovieDetailScreen(mainViewModel)
        }
        composable(Routes.MovieList.screen_route) {
            MovieListScreen(mainViewModel, navController)
        }

    }
}

private fun navigateTo(navController: NavController, routes: String) {

    navController.navigate(routes)
}

/**
 * Composable view for bottom navigation
 * **/
@Composable
fun BottomNavigation(
    bottomBarState: MutableState<Boolean>,
    currentRoute: String,
    navController: NavController,
    mainViewModel: MainViewModel
) {


    val listOfPages = listOf(
        Routes.HomeScreen,
        Routes.SearchMovie,

        )
    AnimatedVisibility(
        visible = bottomBarState.value,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        BottomNavigation(
            backgroundColor = Color(0xFF242424),
            contentColor = Color.Black
        ) {

            listOfPages.forEachIndexed{ index,item ->
                BottomNavigationItem(
                    icon = { Icon(item.icon, contentDescription = item.title) },
                    label = {
                        Text(
                            text = item.title,
                            fontSize = 12.sp
                        )
                    },
                    selectedContentColor = Color(0xFFD3A945),
                    unselectedContentColor = Color.White,
                    alwaysShowLabel = true,
                    selected = currentRoute == item.screen_route,
                    onClick = {
                        mainViewModel.selectedTab =index
                        mainViewModel.errorHttp.value = null
                        navController.navigate(item.screen_route) {

                            navController.graph.startDestinationRoute?.let { screen_route ->
                                popUpTo(screen_route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

