package com.example.lookup.ui

import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lookup.ui.home.HomeScreen
import com.example.lookup.ui.home.HomeViewModel
import com.example.lookup.ui.navigation.LookupDestinations
import com.example.lookup.ui.utils.takePicture

@Composable
fun LookupApp(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = LookupDestinations.HomeScreen.route) {
        composable(LookupDestinations.HomeScreen.route) {
            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current
            val cameraController = remember(context) {
                LifecycleCameraController(context).apply {
                    setEnabledUseCases(CameraController.IMAGE_CAPTURE)
                    bindToLifecycle(lifecycleOwner)
                }
            }
            val homeViewModel = hiltViewModel<HomeViewModel>()
            val homeScreenUiState by homeViewModel.homeScreenUiState.collectAsStateWithLifecycle()
            HomeScreen(
                cameraController = cameraController,
                onShutterButtonClick = {
                    cameraController.takePicture(
                        context = context,
                        onSuccess = {
                            homeViewModel.analyzeImage(it)
                            // unbind to freeze camera
                            cameraController.unbind()
                        },
                        onError = { /*TODO*/ }
                    )
                },
                homeScreenUiState = homeScreenUiState,
                navigateToBookmarkedLocations = { /*TODO*/ },
                onBookmarkIconClick = { /*TODO*/ },
                onSuggestionClick = { /*TODO*/ },
                onBottomSheetDismissed = {
                    // bind to unfreeze camera
                    cameraController.bindToLifecycle(lifecycleOwner)
                }
            )
        }
    }
}

