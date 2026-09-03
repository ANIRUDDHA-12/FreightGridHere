package com.example.FreightGrid.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
@Configuration
public class AiConfig {

    @Value("${GROQ_API_KEY}")
    private String groqApiKey;

    @Bean
    public dev.langchain4j.model.chat.ChatLanguageModel chatLanguageModel() {
        return dev.langchain4j.model.openai.OpenAiChatModel.builder()
                .baseUrl("https://api.groq.com/openai/v1") // Ensure no spaces in this string!
                .apiKey(groqApiKey)
                .modelName("llama-3.3-70b-versatile")
                .temperature(0.1)
                .timeout(Duration.ofSeconds(60))
                .logRequests(true)
                .logResponses(true)
                .build();
    }
}
