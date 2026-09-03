package com.example.FreightGrid.ai.dto;

import com.example.FreightGrid.domain.enums.IncidentCategory;

/**
 * Structured output from the {@link com.example.FreightGrid.ai.agent.PostMortemAgent}.
 * LangChain4j auto-deserialises the LLM's JSON into this record.
 *
 * @param incidentCategory  root-cause classification
 * @param markdownReport    full Executive Summary and RCA in Markdown
 * @param flagForReview     {@code true} if human negligence or high financial loss detected
 */
public record RcaResultDTO(
        IncidentCategory incidentCategory,
        String markdownReport,
        boolean flagForReview
) {}
