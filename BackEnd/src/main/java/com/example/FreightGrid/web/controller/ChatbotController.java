package com.example.FreightGrid.web.controller;

import com.example.FreightGrid.ai.agent.FleetChatbotAgent;
import com.example.FreightGrid.ai.dto.ChatbotResponse;
import com.example.FreightGrid.web.dto.ChatQueryRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoint for the stateful fleet chatbot.
 *
 * <p>Each request carries an {@code X-Session-ID} header that keys into
 * the per-session chat memory, enabling multi-turn conversations.</p>
 */
@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final FleetChatbotAgent fleetChatbotAgent;

    /**
     * Accepts a natural-language query and returns a structured chatbot response.
     *
     * @param sessionId the session identifier (from {@code X-Session-ID} header)
     * @param request   the user's query
     * @return structured response with answer, follow-up suggestions, and escalation flag
     */
    @PostMapping(value = "/ask", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatbotResponse> ask(
            @RequestHeader("X-Session-ID") String sessionId,
            @Valid @RequestBody ChatQueryRequestDTO request) {
        ChatbotResponse response = fleetChatbotAgent.answerQuery(sessionId, request.query());
        
        if (response.suggestedFollowUpQueries() != null && response.suggestedFollowUpQueries().size() > 3) {
            response = new ChatbotResponse(
                    response.markdownAnswer(),
                    response.suggestedFollowUpQueries().subList(0, 3),
                    response.escalationRequired()
            );
        }
        
        return ResponseEntity.ok(response);
    }
}
