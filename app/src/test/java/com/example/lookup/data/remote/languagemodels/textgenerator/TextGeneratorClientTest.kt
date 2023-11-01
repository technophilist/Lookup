package com.example.lookup.data.remote.languagemodels.textgenerator

import com.example.lookup.data.remote.languagemodels.textgenerator.models.MessageDTO
import com.example.lookup.data.remote.languagemodels.textgenerator.models.TextGenerationPromptBody
import com.example.lookup.di.NetworkModule
import kotlinx.coroutines.runBlocking
import org.junit.Test

class TextGeneratorClientTest {

    private val textGeneratorClient = NetworkModule.provideTextGeneratorClient()

    @Test
    fun `Given a valid system & user prompt, the API must successfully return a response with the generated text`() =
        runBlocking {
            val messages = listOf(
                MessageDTO(
                    role = MessageDTO.Roles.SYSTEM,
                    content = "You are a travel guide. Very briefly summarize the history of this place. "
                ),
                MessageDTO(
                    role = MessageDTO.Roles.USER,
                    content = "Washington Monument"
                )
            )
            val textGenerationPromptBody =
                TextGenerationPromptBody(
                    messages = messages,
                    model = TextGenerationPromptBody.Models.GPT_3_5_TURBO,
                    maxResponseTokens = 200
                )
            val generatedTextResponse =
                textGeneratorClient.generateTextForPrompt(textGenerationPromptBody)
            println(generatedTextResponse.body())
        }

}