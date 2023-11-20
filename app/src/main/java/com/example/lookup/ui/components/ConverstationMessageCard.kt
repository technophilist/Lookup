package com.example.lookup.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.lookup.R
import com.example.lookup.domain.home.ConversationMessage

/**
 * A message card composable.
 *
 * @param modifier The modifier to be applied to the message card.
 * @param conversationMessage The message to display.
 */
@Composable
fun ConversationMessageCard(
    modifier: Modifier = Modifier,
    conversationMessage: ConversationMessage
) {
    when (conversationMessage.role) {
        ConversationMessage.Role.Assistant -> AssistantMessageCard(
            modifier = modifier,
            content = conversationMessage.content
        )

        ConversationMessage.Role.User -> UserMessageCard(
            modifier = modifier,
            content = conversationMessage.content
        )
    }
}

/**
 * Message card representing a message sent by the user.
 *
 * @param modifier The [Modifier] to be applied to the message card.
 * @param content The content of the message.
 */
@Composable
private fun UserMessageCard(modifier: Modifier = Modifier, content: String) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .widthIn(max = this.maxWidth / 1.2f),
            content = { Text(modifier = Modifier.padding(16.dp), text = content) }
        )
    }
}

/**
 * Message card representing a message sent by the AI assistant.
 *
 * @param modifier The [Modifier] to be applied to the message card.
 * @param content The content of the message.
 */
@Composable
private fun AssistantMessageCard(modifier: Modifier = Modifier, content: String) {
    val bardIcon = ImageVector.vectorResource(id = R.drawable.ic_bard_logo)
    OutlinedCard(modifier = modifier) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = bardIcon,
                contentDescription = null,
                tint = Color.Unspecified
            )
            Text(text = content)
        }
    }
}