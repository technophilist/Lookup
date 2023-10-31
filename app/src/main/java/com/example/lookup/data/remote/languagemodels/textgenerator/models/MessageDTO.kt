package com.example.lookup.data.remote.languagemodels.textgenerator.models

import com.squareup.moshi.JsonClass

/**
 * A class that represents a single message object that is being transmitted between the
 * large language model API and the local HTTP client.
 * @property role A string that represents the role of the speaker in a conversation. See
 * [Roles] for a list of valid constants that can be used for this property.
 * @property content The content of the message.
 */
@JsonClass(generateAdapter = true)
data class MessageDTO(
    val role: String,
    val content: String
) {
    /**
     * An object that contains valid constants for [MessageDTO.role].
     */
    object Roles {
        /**
        The system role allows you to specify the way the model answers questions.
         */
        const val SYSTEM = "system"

        /**
         * The user role is equivalent to the queries made by the user.
         */
        const val USER = "user"

        /**
         * The assistant role represents the model’s responses based on the user's messages and is
         * seldom used.
         */
        const val ASSISTANT = "assistant"
    }
}
