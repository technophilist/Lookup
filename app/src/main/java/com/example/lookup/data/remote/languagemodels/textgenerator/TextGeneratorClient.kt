package com.example.lookup.data.remote.languagemodels.textgenerator

import com.example.lookup.data.remote.languagemodels.textgenerator.models.GeneratedTextResponse
import com.example.lookup.data.remote.languagemodels.textgenerator.models.TextGenerationPromptBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * This interface represents a client that interacts with remote LLMs.
 */
fun interface TextGeneratorClient {

    /**
     * Returns a response containing the generated text based on the provided prompt.
     * @param promptBody The request body containing the prompt.
     * @return A response containing the generated text.
     */
    @POST(TextGeneratorClientConstants.Endpoints.CHAT_COMPLETION_END_POINT)
    suspend fun generateTextForPrompt(
        @Body promptBody: TextGenerationPromptBody
    ): Response<GeneratedTextResponse>
}