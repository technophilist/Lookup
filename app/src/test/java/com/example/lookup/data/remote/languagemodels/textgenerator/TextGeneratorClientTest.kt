package com.example.lookup.data.remote.languagemodels.textgenerator

import com.example.lookup.data.remote.languagemodels.textgenerator.models.MessageDTO
import com.example.lookup.data.remote.languagemodels.textgenerator.models.TextGenerationPromptBody
import com.example.lookup.data.remote.languagemodels.textgenerator.models.buildTextGenerationPromptBody
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

    @Test
    fun `Given an API response of possible questions, the result must be successfully parsed into valid strings`() =
        runBlocking {
            val systemPrompt = """
                List out 5 very short questions that a traveller might ask a guide about this place.
            """.trimIndent()
            val textGenerationPromptBody = buildTextGenerationPromptBody(
                systemPrompt = systemPrompt,
                userPrompt = "Eiffel Tower",
                maxResponseTokens = 100
            )
            val generatedTextResponse =
                textGeneratorClient.generateTextForPrompt(textGenerationPromptBody)
            val questions =
                generatedTextResponse.body()!!.generatedResponses.first().message.content
            val questionsList = questions.lines().map {
                it.replace(regex = Regex("[0-9]\\.\\s"), replacement = "")
            }
            // all numbers must be removed and the strings must only contain the question.
            val regex = Regex(".*")
            assert(questionsList.all(regex::matches))
        }

}