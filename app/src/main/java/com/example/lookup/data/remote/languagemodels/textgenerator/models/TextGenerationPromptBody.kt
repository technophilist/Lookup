package com.example.lookup.data.remote.languagemodels.textgenerator.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.example.lookup.data.remote.languagemodels.textgenerator.TextGeneratorClientConstants

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