package com.example.FreightGrid.ai.agent;

import com.example.FreightGrid.ai.dto.ChatbotResponse;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

/**
 * Stateful fleet chatbot with per-session memory.
 *
 * <p>Wired to {@code queryTools} for read-only fleet and shipment queries.
 * Uses {@code @MemoryId} to maintain conversation context across requests
 * within the same session.</p>
 */
@AiService(tools = "queryTools")
public interface FleetChatbotAgent {

    @SystemMessage({
            "You are FreightGrid's advanced operational assistant.",
            "You have memory of this conversation — reference prior context when relevant.",
            "You may ONLY answer logistics, fleet, and shipment queries.",
            "You must ALWAYS use your tools to query real data. Never fabricate vehicle IDs or tracking numbers.",
            "Always provide 2-3 suggested follow-up queries related to the current context.",
            "If the user asks to execute a vehicle swap, reroute, or any write operation,",
            "set escalationRequired to true and explain that a human operator must approve.",
            "You MUST respond with ONLY a valid JSON object matching the ChatbotResponse schema:",

            "{\"markdownAnswer\": \"string\", \"suggestedFollowUpQueries\": [\"string\"], \"escalationRequired\": boolean}"
    })
    ChatbotResponse answerQuery(@MemoryId String sessionId, @UserMessage String userQuery);
}
