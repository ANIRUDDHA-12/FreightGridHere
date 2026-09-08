package com.example.FreightGrid.ai.agent;

import com.example.FreightGrid.ai.dto.ChatbotResponse;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.TokenStream;
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
            "If the user asks to execute a vehicle swap, reroute, or any write operation,",
            "explain that a human operator must approve.",
            "You MUST output pure Markdown text. Do NOT use JSON formatting."
    })
    TokenStream answerQueryStream(@MemoryId String sessionId, @UserMessage String userQuery);
}
