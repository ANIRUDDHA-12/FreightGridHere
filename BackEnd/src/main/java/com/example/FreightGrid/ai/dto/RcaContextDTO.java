package com.example.FreightGrid.ai.dto;

/**
 * Input context for the {@link com.example.FreightGrid.ai.agent.PostMortemAgent}.
 * Serialized to JSON before being passed as {@code @UserMessage}.
 *
 * @param trackingNumber  the shipment tracking number
 * @param trigger         the disruption trigger description
 * @param cost            the estimated financial cost of the incident
 * @param strategy        the executed mitigation strategy
 * @param telemetryData   raw telemetry sensor readings (JSON) for RCA, if available
 */
public record RcaContextDTO(
        String trackingNumber,
        String trigger,
        double cost,
        String strategy,
        String telemetryData
) {}
