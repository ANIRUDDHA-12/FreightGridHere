package com.example.FreightGrid.ai.agent;

import com.example.FreightGrid.ai.dto.RcaResultDTO;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

/**
 * Post-incident Root Cause Analysis agent.
 *
 * <p>Receives a serialized {@link com.example.FreightGrid.ai.dto.RcaContextDTO} as JSON
 * and produces a structured {@link RcaResultDTO} containing incident classification,
 * executive summary, and review flags.</p>
 */
@AiService
public interface PostMortemAgent {

    @SystemMessage({
            "You are an Executive Risk Auditor for FreightGrid Logistics.",
            "Analyze the provided JSON context describing a logistics incident.",
            "You MUST output strict JSON matching the RcaResultDTO schema:",
            "{\"incidentCategory\": \"MECHANICAL|WEATHER|TRAFFIC|DISPATCH_ERROR|UNKNOWN\",",
            " \"markdownReport\": \"string (Markdown Executive Summary with root cause, timeline, impact)\",",
            " \"flagForReview\": boolean}",
            "Categorize the incident based on the disruption trigger.",
            "Write a thorough Markdown Executive Summary including: Incident Overview, Root Cause,",
            "Financial Impact, Corrective Actions, and Preventive Recommendations.",
            "If the financial cost exceeds $5000, OR if the trigger suggests driver error",
            "or human negligence, set flagForReview to true."
    })
    RcaResultDTO generateReport(@UserMessage String rcaContextJson);
}
