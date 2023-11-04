package com.example.lookup.data.remote.languagemodels.textgenerator.models

import androidx.compose.material3.Text
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A class that contains the prompt messages and it's associated meta-data that a large language
 * model API uses to generate a response in the form of [GeneratedTextResponse].
 * The [maxResponseTokens] property is used to set an upperbound for the number of tokens that the
 * model will generate in the completion.
 * @property messages A list of [MessageDTO]'s containing the prompts.
 * @property model The model to be used for generating text. See [TextGenerationPromptBody.Models]
 * for possible values of this property.
 * @property maxResponseTokens The maximum number of tokens to generate in the chat completion.
 */
@JsonClass(generateAdapter = true)
data class TextGenerationPromptBody(
    val messages: List<MessageDTO>,
    val model: String,
    @Json(name = "max_tokens") val maxResponseTokens: Int = 150
) {
    /**
     * An object that contains valid constants for [TextGenerationPromptBody.model].
     */
    object Models {
        /**
         * This constant represents the gpt-3.5-turbo model.
         */
        const val GPT_3_5_TURBO = "gpt-3.5-turbo-0613"
    }

}

/**
 * A convenience builder function used to build an instance of [TextGenerationPromptBody]
 * with the provided [systemPrompt] and [userPrompt].
 */
fun buildTextGenerationPromptBody(
    systemPrompt: String,
    userPrompt: String,
    maxResponseTokens: Int = 150,
    model: String = TextGenerationPromptBody.Models.GPT_3_5_TURBO
) = TextGenerationPromptBody(
    messages = listOf(
        MessageDTO(
            role = MessageDTO.Roles.SYSTEM,
            content = systemPrompt
        ),
        MessageDTO(
            role = MessageDTO.Roles.USER,
            content = userPrompt
        )
    ),
    model = model,
    maxResponseTokens = maxResponseTokens
)