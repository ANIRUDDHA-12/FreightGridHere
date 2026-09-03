package com.example.FreightGrid.web.dto;

public record SimulateIncidentResponseDTO(
    String trackingNumber,
    String finalStrategy,
    boolean approved,
    int retriesUsed,
    boolean requiresDirectorApproval,
    double estimatedCost
) {}
