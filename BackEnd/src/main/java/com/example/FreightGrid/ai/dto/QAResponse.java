package com.example.FreightGrid.ai.dto;

/**
 * Structured response from the {@link com.example.FreightGrid.ai.agent.QualityAssuranceAgent}.
 *
 * <p>LangChain4j deserialises the LLM's JSON output into this record automatically
 * when the agent method's return type is {@code QAResponse} instead of {@code String}.</p>
 *
 * @param approved  {@code true} if the proposed strategy passed QA validation
 * @param reasoning human-readable justification for the approval or rejection
 */
public record QAResponse(boolean approved, String reasoning) {

    // ── Legacy JavaBean getters for frameworks that expect get*/is* convention ──

    public boolean isApproved() {
        return approved();
    }

    public String getReasoning() {
        return reasoning();
    }
}
