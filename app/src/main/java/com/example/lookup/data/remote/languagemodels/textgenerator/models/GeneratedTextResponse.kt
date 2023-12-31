package com.example.lookup.data.remote.languagemodels.textgenerator.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A class that models responses generated by a large language model.
 * @property id A unique identifier for the chat completion.
 * @property createdAtTimestampSeconds The Unix timestamp (in seconds) of when the chat
 * completion was created.
 * @property generatedResponses A list of generated responses.
 */
@JsonClass(generateAdapter = true)
data class GeneratedTextResponse(
    val id: String,
    @Json(name = "created") val createdAtTimestampSeconds: Int,
    @Json(name = "choices") val generatedResponses: List<GeneratedResponse>
) {
    /**
     * A class that models a single generated response by a large language model.
     */
    @JsonClass(generateAdapter = true)
    data class GeneratedResponse(val message: MessageDTO)
}

/**
 * Convenience extension property. Short hand for "generatedTextResponse.generatedResponses.first.message.content"
 */
val GeneratedTextResponse.firstResponse get() = generatedResponses.first().message.content
