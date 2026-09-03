package com.example.FreightGrid.config;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides session-scoped chat memory for the {@link com.example.FreightGrid.ai.agent.FleetChatbotAgent}.
 *
 * <p>Each unique {@code sessionId} (passed via {@code @MemoryId}) gets its own
 * {@link MessageWindowChatMemory} instance, retaining the last 10 messages.
 * Sessions are stored in an in-memory {@link ConcurrentHashMap}.</p>
 */
@Configuration
public class ChatMemoryConfig {

    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        Map<Object, MessageWindowChatMemory> memories = new ConcurrentHashMap<>();

        return memoryId -> memories.computeIfAbsent(memoryId, id ->
                MessageWindowChatMemory.withMaxMessages(10)
        );
    }
}
