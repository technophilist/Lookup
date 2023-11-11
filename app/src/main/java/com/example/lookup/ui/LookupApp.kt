package com.example.lookup.ui

import android.view.Surface
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lookup.di.LookupApplication
import com.example.lookup.ui.home.HomeScreen
import com.example.lookup.ui.home.HomeScreenUiState
import com.example.lookup.ui.home.HomeViewModel
import com.example.lookup.ui.home.IdentifiedLocation
import com.example.lookup.ui.navigation.LookupDestinations
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@Composable
fun LookupApp(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = LookupDestinations.HomeScreen.route) {
        composable(LookupDestinations.HomeScreen.route) {
            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current
            val cameraController = remember(context) {
                LifecycleCameraController(context).apply {
                    setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
                    bindToLifecycle(lifecycleOwner)
                }
            }
            HomeScreen(
                cameraController = cameraController,
                onShutterButtonClick = {/*TODO*/ },
                homeScreenUiState = HomeScreenUiState(),
                navigateToBookmarkedLocations = { /*TODO*/ },
                onBookmarkIconClick = { /*TODO*/ },
                onSuggestionClick = {/*TODO*/ }
            )
        }
    }
}