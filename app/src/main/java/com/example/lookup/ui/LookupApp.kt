package com.example.lookup.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lookup.di.LookupApplication
import com.example.lookup.ui.home.HomeScreen
import com.example.lookup.ui.home.HomeScreenUiState
import com.example.lookup.ui.home.IdentifiedLocation
import com.example.lookup.ui.navigation.LookupDestinations
import kotlinx.coroutines.delay

@Composable
fun LookupApp(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = LookupDestinations.HomeScreen.route) {
        composable(LookupDestinations.HomeScreen.route) {
            HomeScreen(
                homeScreenUiState = HomeScreenUiState(),
                navigateToBookmarkedLocations = { /*TODO*/ },
                onBookmarkIconClick = { /*TODO*/ },
                onSuggestionClick = {/*TODO*/ }
            )
        }
    }
}