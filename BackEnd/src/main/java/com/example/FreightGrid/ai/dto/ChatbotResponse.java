package com.example.FreightGrid.ai.dto;

import java.util.List;

/**
 * Structured response from the {@link com.example.FreightGrid.ai.agent.FleetChatbotAgent}.
 *
 * @param markdownAnswer            the chatbot's answer in Markdown format
 * @param suggestedFollowUpQueries  up to 3 actionable follow-up query suggestions
 * @param escalationRequired        {@code true} if the user is requesting a manual swap/execution
 */
public record ChatbotResponse(
        String markdownAnswer,
        List<String> suggestedFollowUpQueries,
        boolean escalationRequired
) {}
