package com.example.FreightGrid.web.controller;

import com.example.FreightGrid.ai.agent.FleetChatbotAgent;
import com.example.FreightGrid.web.dto.ChatQueryRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import dev.langchain4j.service.TokenStream;
import java.io.IOException;

/**
 * REST endpoint for the stateful fleet chatbot.
 *
 * <p>Each request carries an {@code X-Session-ID} header that keys into
 * the per-session chat memory, enabling multi-turn conversations.</p>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final FleetChatbotAgent fleetChatbotAgent;

    /**
     * Accepts a natural-language query and streams the chatbot response via SSE.
     *
     * @param sessionId the session identifier (from {@code X-Session-ID} header)
     * @param request   the user's query
     * @return SseEmitter streaming thought, token, and done events
     */
    @PostMapping(value = "/ask", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter ask(
            @RequestHeader("X-Session-ID") String sessionId,
            @Valid @RequestBody ChatQueryRequestDTO request) {
            
        SseEmitter emitter = new SseEmitter(60_000L);
        
        emitter.onCompletion(() -> log.info("SSE Completed"));
        emitter.onTimeout(emitter::complete);
        emitter.onError(ex -> log.error("SSE Error", ex));

        TokenStream stream = fleetChatbotAgent.answerQueryStream(sessionId, request.query());
        
        stream
            .onNext(token -> {
                try {
                    String escapedToken = token.replace("\\", "\\\\")
                                               .replace("\"", "\\\"")
                                               .replace("\n", "\\n")
                                               .replace("\r", "\\r");
                    emitter.send(SseEmitter.event()
                        .name("token")
                        .data("{\"content\": \"" + escapedToken + "\"}"));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            })
            .onToolExecuted(tool -> {
                try {
                    String toolName = tool.name();
                    emitter.send(SseEmitter.event()
                        .name("thought")
                        .data("{\"content\": \"Executing: " + toolName + "\"}"));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            })
            .onComplete(response -> {
                try {
                    emitter.send(SseEmitter.event()
                        .name("done")
                        .data("{}"));
                    emitter.complete();
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            })
            .onError(emitter::completeWithError)
            .start();

        return emitter;
    }
}
