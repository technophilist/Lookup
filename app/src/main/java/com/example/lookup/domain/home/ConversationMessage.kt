package com.example.lookup.domain.home

/**
 * Represents a single message in a conversation.
 *
 * @property role The role of the participant who sent the message.
 * @property content The content of the message.
 */
data class ConversationMessage(
    val role: Role,
    val content: String
) {
    /**
     * An enum value that indicates the role of the participant who sent the message.
     */
    enum class Role {
        /**
         * Represents an AI assistant.
         */
        Assistant,

        /**
         * Represents the user..
         */
        User
    }
}