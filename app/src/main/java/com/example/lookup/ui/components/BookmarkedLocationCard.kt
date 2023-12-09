package com.example.lookup.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * A card that displays the details of a bookmarked location. Please note that this composable
 * has a **max height of 300dp.**
 *
 * @param nameOfLocation The name of the bookmarked location.
 * @param imageUrlOfLocation The URL of the image associated with the bookmarked location.
 * @param modifier [Modifier] to be applied to the composable
 */
@Composable
fun BookmarkedLocationCard(
    nameOfLocation: String,
    imageUrlOfLocation: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .sizeIn(maxHeight = 300.dp)
            .then(modifier)
    ) {
        AsyncImage(
            modifier = Modifier.weight(1f),
            model = imageUrlOfLocation,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier.padding(8.dp),
            text = nameOfLocation,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 2,
            minLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}