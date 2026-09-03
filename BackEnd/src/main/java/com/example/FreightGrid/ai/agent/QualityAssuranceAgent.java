package com.example.FreightGrid.ai.agent;

import com.example.FreightGrid.ai.dto.QAResponse;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

/**
 * Final Quality Assurance gate in the multi-agent swarm.
 *
 * <p>No tools — this agent is a pure LLM evaluator that receives the combined
 * Dispatcher + Financial proposal and returns a typed {@link QAResponse}.</p>
 *
 * <p>LangChain4j automatically deserialises the LLM's JSON output into the
 * {@code QAResponse} record when the return type is declared accordingly.</p>
 */
@AiService
public interface QualityAssuranceAgent {

    @SystemMessage({
            "You are the final Quality Assurance gate for FreightGrid Logistics.",
            "You receive a combined proposal that includes both the Dispatcher's rescue plan",
            "and the Financial Agent's cost analysis.",
            "Your job is to evaluate the overall strategy for feasibility, risk, and cost-effectiveness.",
            "You MUST respond with ONLY a valid JSON object — no markdown, no explanation outside the JSON.",
            "The JSON schema is: {\"approved\": boolean, \"reasoning\": \"string\"}.",
            "Set 'approved' to true ONLY if the strategy is financially sound, operationally feasible,",
            "and does not violate SLA constraints. Otherwise set it to false with a clear reasoning."
    })
    QAResponse validateStrategy(@UserMessage String combinedProposal);
}
