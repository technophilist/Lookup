package com.example.lookup.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.LifecycleCameraController
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
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
import com.example.lookup.domain.home.ConversationMessage
import com.example.lookup.domain.home.IdentifiedLocation
import com.example.lookup.ui.components.AssistantMessageCard
import com.example.lookup.ui.components.ShutterButton
import com.example.lookup.ui.components.UserMessageCard
import com.example.lookup.ui.utils.BookmarkAdd
import com.example.lookup.ui.utils.BookmarkAdded
import com.example.lookup.ui.utils.Bookmarks
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    cameraController: LifecycleCameraController,
    homeScreenUiState: HomeScreenUiState,
    navigateToBookmarkedLocations: () -> Unit,
    onBookmarkIconClick: () -> Unit,
    onSuggestionClick: (index: Int) -> Unit,
    onShutterButtonClick: () -> Unit,
    onBottomSheetDismissed: () -> Unit,
    onErrorDialogDismissRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    HomeScreen(
        modifier = modifier,
        cameraController = cameraController,
        onShutterButtonClick = onShutterButtonClick,
        identifiedLocation = homeScreenUiState.identifiedLocation,
        conversationMessages = homeScreenUiState.conversationMessages,
        isAnalyzing = homeScreenUiState.isAnalyzing,
        hasErrorOccurredWhenAnalyzing = homeScreenUiState.errorOccurredWhenAnalyzing,
        navigateToBookmarkedLocations = navigateToBookmarkedLocations,
        onBookmarkIconClick = onBookmarkIconClick,
        onBottomSheetDismissed = onBottomSheetDismissed,
        onErrorDialogDismissRequested = onErrorDialogDismissRequested,
        onSuggestionClick = onSuggestionClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    cameraController: LifecycleCameraController,
    identifiedLocation: IdentifiedLocation?,
    conversationMessages: List<ConversationMessage>,
    isAnalyzing: Boolean,
    hasErrorOccurredWhenAnalyzing: Boolean,
    navigateToBookmarkedLocations: () -> Unit,
    onBookmarkIconClick: () -> Unit,
    onSuggestionClick: (index: Int) -> Unit,
    onShutterButtonClick: () -> Unit,
    onBottomSheetDismissed: () -> Unit,
    onErrorDialogDismissRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isCameraPermissionGranted by remember { mutableStateOf(isCameraPermissionGranted(context)) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isCameraPermissionGranted = it }
    )
    // without this, the bottom sheet will keep closing and opening whenever the
    // value of any state is changed
    val updatedOnBottomSheetDismissed by rememberUpdatedState(newValue = onBottomSheetDismissed)
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = {
            if (it == SheetValue.Hidden) updatedOnBottomSheetDismissed()
            true
        }
    )
    var isBottomSheetVisible by remember(identifiedLocation) { mutableStateOf(identifiedLocation != null) }
    val analyzingAnimationComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(
            R.raw.anayzing_animation
        )
    )
    val localHapticFeedback = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        cameraPermissionLauncher.launch(REQUIRED_CAMERA_PERMISSION)
    }

    Box(modifier = modifier) {
        if (isCameraPermissionGranted) {
            CameraPreview(modifier = Modifier.fillMaxSize(), controller = cameraController)
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
                    fontWeight = FontWeight.Bold,
                    color = Color.White // must be white in both light & dark modes
                )
            },
            actions = { TopBarActionsRow(onBookmarksButtonClick = navigateToBookmarkedLocations) }
        )
        if (isAnalyzing && !hasErrorOccurredWhenAnalyzing) {
            LottieAnimation(
                modifier = Modifier.align(Alignment.Center),
                composition = analyzingAnimationComposition,
                reverseOnRepeat = true,
                iterations = LottieConstants.IterateForever
            )
        }
        if (isCameraPermissionGranted && !isAnalyzing) {
            ShutterButton(
                onClick = onShutterButtonClick,
                modifier = Modifier
                    .navigationBarsPadding()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
    LaunchedEffect(identifiedLocation) {
        // perform haptic feedback when a location is identified
        if (identifiedLocation != null) localHapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }
    if (isBottomSheetVisible) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = { isBottomSheetVisible = false },
            windowInsets = WindowInsets(0, 0, 0, 0),
            content = {
                identifiedLocation?.let {
                    BottomSheetContent(
                        modifier = Modifier.fillMaxSize(),
                        identifiedLocation = it,
                        conversationMessages = conversationMessages,
                        onBookmarkIconClick = onBookmarkIconClick,
                        onSuggestionClick = onSuggestionClick
                    )
                }
            }
        )
    }
    if (hasErrorOccurredWhenAnalyzing) {
        AlertDialog(
            title = { Text(text = "An error occurred") },
            text = { Text(text = "Oops! An error occurred when trying to analyzing the image. Please try again.") },
            onDismissRequest = onErrorDialogDismissRequested,
            confirmButton = {
                TextButton(onClick = onErrorDialogDismissRequested, content = { Text("Okay") })
            }
        )
    }
}

@Composable
private fun BottomSheetContent(
    identifiedLocation: IdentifiedLocation,
    conversationMessages: List<ConversationMessage>,
    onSuggestionClick: (index: Int) -> Unit,
    onBookmarkIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Assistant messages very often take some time to generate. While the result is being
    // generated, there wont be any text for the message card composables to render.
    // Once a message is ready, this state map could as a way to indicate to compose that
    // the message is ready.
    val assistantMessageMap = remember {
        mutableStateMapOf<ConversationMessage.AssistantMessage, String>()
    }
    val lazyListState = rememberLazyListState()
    LaunchedEffect(conversationMessages) {
        conversationMessages.lastIndex.takeIf { it >= 0 }
            ?.let { lazyListState.animateScrollToItem(it) }
    }
    Column(modifier = modifier) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // header
            bottomSheetHeaderItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                locationName = identifiedLocation.name,
                isBookmarked = identifiedLocation.isBookmarked,
                onBookmarkIconClick = onBookmarkIconClick
            )
            // images
            bottomSheetImagesRowItem(imageUrls = identifiedLocation.imageUrls)
            // messages
            conversationMessages(
                conversationMessages = conversationMessages,
                assistantMessageMap = assistantMessageMap,
                itemSuspendEffectHandlerBlock = { conversationMessage ->
                    delay(50)
                    if (conversationMessage !is ConversationMessage.AssistantMessage) return@conversationMessages
                    assistantMessageMap[conversationMessage] = when (conversationMessage.content) {
                        is ConversationMessage.AssistantMessage.Content.DeferredContent -> conversationMessage.content.deferredContent.await()
                        is ConversationMessage.AssistantMessage.Content.Immediate -> conversationMessage.content.immediateContent
                    }
                }
            )
        }
        BottomSheetSuggestionsRow(
            modifier = Modifier.navigationBarsPadding(),
            moreInfoSuggestions = identifiedLocation.moreInfoSuggestions,
            onSuggestionClick = onSuggestionClick
        )
    }
}

private fun LazyListScope.conversationMessages(
    conversationMessages: List<ConversationMessage>,
    assistantMessageMap: Map<ConversationMessage.AssistantMessage, String>,
    itemSuspendEffectHandlerBlock: suspend (ConversationMessage) -> Unit
) {
    // todo: automatically scroll to the latest message
    items(conversationMessages) { conversationMessage ->
        LaunchedEffect(Unit) { itemSuspendEffectHandlerBlock(conversationMessage) }
        when (conversationMessage) {
            is ConversationMessage.AssistantMessage -> AssistantMessageCard(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                message = assistantMessageMap[conversationMessage]
            )

            is ConversationMessage.UserMessage -> UserMessageCard(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                message = conversationMessage.content
            )
        }
    }
}

private fun LazyListScope.bottomSheetHeaderItem(
    locationName: String,
    isBookmarked: Boolean,
    onBookmarkIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    item {
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
}

private fun LazyListScope.bottomSheetImagesRowItem(
    imageUrls: List<String>,
    modifier: Modifier = Modifier
) {
    item {
        LazyRow(
            modifier = modifier,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items = imageUrls, key = { it }) {
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
    }
}

@Composable
private fun BottomSheetSuggestionsRow(
    moreInfoSuggestions: List<IdentifiedLocation.MoreInfoSuggestion>,
    onSuggestionClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        itemsIndexed(moreInfoSuggestions) { index, moreInfoSuggestion ->
            SuggestionChip(
                onClick = { onSuggestionClick(index) },
                label = {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = moreInfoSuggestion.suggestion
                    )
                }
            )
        }
    }
}

@Composable
private fun TopBarActionsRow(
    onBookmarksButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            OutlinedIconButton(
                modifier = Modifier.align(Alignment.Center),
                onClick = onBookmarksButtonClick,
                content = { Icon(imageVector = Icons.Rounded.Bookmarks, contentDescription = null) }
            )
        }
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

