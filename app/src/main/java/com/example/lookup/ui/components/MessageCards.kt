package com.example.lookup.ui.components

import android.os.Build.VERSION
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.example.lookup.R

/**
 * Message card representing a [message] sent by the user.
 *
 * @param modifier The [Modifier] to be applied to the message card.
 */
@Composable
fun UserMessageCard(modifier: Modifier = Modifier, message: String) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .widthIn(max = this.maxWidth / 1.2f),
            content = { Text(modifier = Modifier.padding(16.dp), text = message) }
        )
    }
}

/**
 * Message card representing a [message] sent by the assistant. If the message is null, then
 * this composable will display a loading animation
 *
 * @param modifier The [Modifier] to be applied to the message card.
 */
@Composable
fun AssistantMessageCard(modifier: Modifier = Modifier, message: String?) {
    val context = LocalContext.current
    val bardIcon = ImageVector.vectorResource(id = R.drawable.ic_bard_logo)
    val imageLoader = remember(context) {
        ImageLoader(context)
            .newBuilder()
            .components {
                if (VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
                else add(GifDecoder.Factory())
            }
            .build()
    }
    val imageRequest = remember(imageLoader) {
        ImageRequest.Builder(context)
            .data(R.drawable.bard_sparkle_thinking_anim)
            .size(Size.ORIGINAL)
            .build()
    }
    val asyncImagePainter = rememberAsyncImagePainter(
        model = imageRequest,
        imageLoader = imageLoader
    )
    Card(modifier = modifier) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            if (message == null) {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = asyncImagePainter,
                    contentDescription = null
                )
            } else {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = bardIcon,
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
            AnimatedVisibility(
                visible = message != null,
                enter = fadeIn(tween(durationMillis = 600)) + expandVertically(expandFrom = Alignment.Top),
                content = { Text(text = message ?: "") }
            )
        }
    }
}