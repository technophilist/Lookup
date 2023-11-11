package com.example.lookup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

/**
 * A shutter button composable that has a haptic feedback when it's clicked.
 *
 * @param onClick The callback to be invoked when the button is clicked.
 * @param modifier [Modifier] to be applied to the composable.
 */
@Composable
fun ShutterButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val localHapticFeedback = LocalHapticFeedback.current
    Box(
        modifier = modifier
            .size(96.dp)
            .clip(CircleShape)
            .border(2.dp, Color.White, CircleShape)
            .padding(8.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable {
                onClick()
                localHapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.Center)
                .size(32.dp),
            imageVector = Icons.Outlined.Search,
            tint = Color.Black,
            contentDescription = null
        )
    }
}