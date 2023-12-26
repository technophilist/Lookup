package com.example.lookup.ui.landmarkdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.lookup.R
import com.example.lookup.domain.landmarkdetail.ArticleVariation
import com.example.lookup.domain.landmarkdetail.LandmarkArticle


@Composable
fun LandmarkDetailScreen(
    uiState: LandmarkDetailScreenUiState,
    onVariationClick: (ArticleVariation) -> Unit,
    onRetryButtonClick: () -> Unit,
    onBackButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is LandmarkDetailScreenUiState.Loading -> LoadingScreen(
            modifier = modifier,
            onBackButtonClick = onBackButtonClick
        )

        is LandmarkDetailScreenUiState.ArticleLoaded -> {
            LandmarkDetailScreen(
                modifier = modifier,
                article = uiState.landmarkArticle,
                currentlySelectedArticleVariation = uiState.currentlySelectedArticleVariation,
                onVariationClick = onVariationClick,
                onBackButtonClick = onBackButtonClick
            )
        }

        is LandmarkDetailScreenUiState.Error -> ErrorScreen(
            modifier = modifier,
            onRetryButtonClick = onRetryButtonClick,
            onBackButtonClick = onBackButtonClick
        )
    }
}

@Composable
private fun LoadingScreen(modifier: Modifier = Modifier, onBackButtonClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        contentAlignment = Alignment.Center,
        content = {
            IconButton(
                modifier = Modifier
                    .statusBarsPadding()
                    .align(Alignment.TopStart),
                onClick = onBackButtonClick,
                content = { Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null) }
            )
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    )
}

@Composable
private fun ErrorScreen(
    onRetryButtonClick: () -> Unit,
    onBackButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        contentAlignment = Alignment.Center,
        content = {
            IconButton(
                modifier = Modifier
                    .statusBarsPadding()
                    .align(Alignment.TopStart),
                onClick = onBackButtonClick,
                content = { Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null) }
            )
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Oops! An error occurred when loading the article. Please try again."
                )
                OutlinedButton(
                    onClick = onRetryButtonClick,
                    content = { Text(text = "Retry") }
                )
            }
        }
    )
}

@Composable
private fun LandmarkDetailScreen(
    article: LandmarkArticle,
    currentlySelectedArticleVariation: ArticleVariation,
    onVariationClick: (ArticleVariation) -> Unit,
    onBackButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Header(
                title = article.nameOfLandmark,
                subtitle = article.oneLinerAboutLandmark,
                heroImageUrl = article.imageUrl,
                availableArticleVariations = article.availableArticleVariations,
                currentlySelectedVariation = currentlySelectedArticleVariation,
                onArticleVariationClick = onVariationClick,
                onBackButtonClick = onBackButtonClick
            )
        }
        item {
            Text(
                modifier = Modifier
                    .padding(16.dp)
                    .alpha(0.9f)
                    .navigationBarsPadding(),
                style = MaterialTheme.typography.bodyLarge,
                text = currentlySelectedArticleVariation.content
            )
        }
    }
}

@Composable
private fun Header(
    title: String,
    subtitle: String,
    heroImageUrl: String,
    currentlySelectedVariation: ArticleVariation,
    availableArticleVariations: List<ArticleVariation>,
    onArticleVariationClick: (ArticleVariation) -> Unit,
    onBackButtonClick: () -> Unit,
) {
    var isArticleVariationsDropDownMenuVisible by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.size(500.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize(),
            model = heroImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        // apply black scrim over the image to improve visibility of overlaid text
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                modifier = Modifier.alpha(0.5f),
                text = subtitle,
                style = MaterialTheme.typography.titleLarge,
                fontStyle = FontStyle.Italic
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBackButtonClick,
                content = { Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null) }
            )
            ArticleVariationDropDownMenu(
                modifier = Modifier.padding(end = 16.dp),
                isVisible = isArticleVariationsDropDownMenuVisible,
                currentlySelectedVariation = currentlySelectedVariation,
                availableVariations = availableArticleVariations,
                onVariationClick = onArticleVariationClick,
                onDropDownIconClick = { isArticleVariationsDropDownMenuVisible = true },
                onDismissRequest = { isArticleVariationsDropDownMenuVisible = false }
            )
        }
    }
}

@Composable
private fun ArticleVariationDropDownMenu(
    isVisible: Boolean,
    currentlySelectedVariation: ArticleVariation,
    availableVariations: List<ArticleVariation>,
    onVariationClick: (ArticleVariation) -> Unit,
    onDropDownIconClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bardIcon = ImageVector.vectorResource(id = R.drawable.ic_bard_logo)
    Box(modifier = modifier) {
        OutlinedButton(onClick = onDropDownIconClick) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = bardIcon,
                contentDescription = null
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = currentlySelectedVariation.variationType.label,
                style = MaterialTheme.typography.labelLarge
            )
        }
        DropdownMenu(expanded = isVisible, onDismissRequest = onDismissRequest) {
            availableVariations.forEach { variation ->
                DropdownMenuItem(
                    text = { Text(text = variation.variationType.label) },
                    onClick = { onVariationClick(variation) }
                )
            }
        }
    }
}