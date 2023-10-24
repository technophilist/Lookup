package com.example.lookup.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.lookup.ui.components.CameraPreview
import com.example.lookup.R
import com.example.lookup.ui.utils.BookmarkAdd
import com.example.lookup.ui.utils.BookmarkAdded
import com.example.lookup.ui.utils.Bookmarks

/**
 * A data class representing an identified location.
 *
 * @property name The name of the location.
 * @property imageUrls A list of URLs to images of the location.
 * @property infoCardsContentList A list of [InfoCardContent] objects containing supplementary
 * information about the location.
 * @property isBookmarked A boolean indicating whether the location has been bookmarked.
 */
data class IdentifiedLocation(
    val name: String,
    val imageUrls: List<String>,
    val infoCardsContentList: List<InfoCardContent>,
    val isBookmarked: Boolean
) {

    /**
     * A data class representing a singular piece of information that will be displayed as a
     * card in the [HomeScreen].
     *
     * @property title The title of card.
     * @property content The content of the card.
     */
    data class InfoCardContent(
        val title: String,
        val content: String
    )
}

@Composable
fun HomeScreen(
    homeScreenUiState: HomeScreenUiState,
    navigateToBookmarkedLocations: () -> Unit,
    onBookmarkIconClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HomeScreen(
        modifier = modifier,
        identifiedLocation = homeScreenUiState.identifiedLocation,
        isAnalyzing = false,
        navigateToBookmarkedLocations = navigateToBookmarkedLocations,
        onBookmarkIconClick = onBookmarkIconClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    identifiedLocation: IdentifiedLocation?,
    isAnalyzing: Boolean,
    navigateToBookmarkedLocations: () -> Unit,
    onBookmarkIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isCameraPermissionGranted by remember { mutableStateOf(isCameraPermissionGranted(context)) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isCameraPermissionGranted = it }
    )
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
        }
    }
    val bottomSheetState = rememberModalBottomSheetState()
    var isBottomSheetVisible by remember(identifiedLocation) { mutableStateOf(identifiedLocation != null) }
    val analyzingAnimationComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(
            R.raw.anayzing_animation
        )
    )

    LaunchedEffect(Unit) {
        cameraPermissionLauncher.launch(REQUIRED_CAMERA_PERMISSION)
    }

    Box(modifier = modifier) {
        if (isCameraPermissionGranted) {
            CameraPreview(modifier = Modifier.fillMaxSize(), controller = controller)
        } else {
            DefaultBackground(modifier = Modifier.fillMaxSize())
        }
        TopAppBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
            title = {
                Text(
                    text = stringResource(id = R.string.app_name),
                    fontWeight = FontWeight.Bold
                )
            },
            actions = { TopBarActionsRow(onBookmarksButtonClick = navigateToBookmarkedLocations) }
        )
        if (isAnalyzing) {
            LottieAnimation(
                modifier = Modifier.align(Alignment.Center),
                composition = analyzingAnimationComposition,
                reverseOnRepeat = true,
                iterations = LottieConstants.IterateForever
            )
        }
    }
    if (isBottomSheetVisible) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = { isBottomSheetVisible = false },
            windowInsets = WindowInsets(0, 0, 0, 0),
            content = {
                identifiedLocation?.let {
                    BottomSheetContent(
                        identifiedLocation = it,
                        onBookmarkIconClick = onBookmarkIconClick
                    )
                }
            }
        )
    }
}

@Composable
private fun BottomSheetContent(
    identifiedLocation: IdentifiedLocation,
    onBookmarkIconClick: () -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState()
) {
    Column(
        modifier = modifier.verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BottomSheetHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            locationName = identifiedLocation.name,
            isBookmarked = identifiedLocation.isBookmarked,
            onBookmarkIconClick = onBookmarkIconClick
        )
        // images
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(identifiedLocation.imageUrls) {
                AsyncImage(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    model = it,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
        }
        // Not using lazy list because very few items are expected to be in the list
        identifiedLocation.infoCardsContentList.forEach {
            IdentifiedLocationInfoCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                identifiedLocation = it
            )
        }
        Spacer(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(16.dp)
        )
    }
}

@Composable
private fun BottomSheetHeader(
    locationName: String,
    isBookmarked: Boolean,
    onBookmarkIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val icon = if (isBookmarked) Icons.Rounded.BookmarkAdded else Icons.Rounded.BookmarkAdd
        Text(
            modifier = Modifier.weight(1f),
            text = locationName,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.displaySmall
        )
        OutlinedIconButton(
            onClick = onBookmarkIconClick,
            content = { Icon(imageVector = icon, contentDescription = null) }
        )
    }
}

@Composable
private fun IdentifiedLocationInfoCard(
    modifier: Modifier = Modifier,
    identifiedLocation: IdentifiedLocation.InfoCardContent
) {
    OutlinedCard(modifier = modifier) {
        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            text = identifiedLocation.title,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            text = identifiedLocation.content
        )
    }
}

@Composable
private fun TopBarActionsRow(
    onBookmarksButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        IconButton(
            onClick = onBookmarksButtonClick,
            content = { Icon(imageVector = Icons.Rounded.Bookmarks, contentDescription = null) }
        )
    }
}

@Composable
private fun DefaultBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = null
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "Camera permission not granted")
        }
    }
}

private fun isCameraPermissionGranted(context: Context) = ContextCompat.checkSelfPermission(
    context,
    REQUIRED_CAMERA_PERMISSION
) == PackageManager.PERMISSION_GRANTED

private const val REQUIRED_CAMERA_PERMISSION = Manifest.permission.CAMERA

