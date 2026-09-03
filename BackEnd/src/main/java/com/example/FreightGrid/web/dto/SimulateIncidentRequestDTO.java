package com.example.FreightGrid.web.dto;

public record SimulateIncidentRequestDTO(
    String trackingNumber,
    String disruptionType,
    double estimatedDelayHours,
    String telemetryData
) {}
