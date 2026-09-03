package com.example.FreightGrid.web.dto;

/**
 * Inbound telemetry ping from a vehicle's IoT/GPS unit.
 *
 * @param vehicleCode               the vehicle sending telemetry (e.g., "VH-003")
 * @param currentLatitude            GPS latitude
 * @param currentLongitude           GPS longitude
 * @param currentSpeedMph            current speed in miles per hour
 * @param engineTemperatureCelsius   engine temperature in °C
 * @param trafficDelayMinutes        accumulated traffic delay in minutes
 */
public record TelemetryPingDTO(
        String vehicleCode,
        double currentLatitude,
        double currentLongitude,
        double currentSpeedMph,
        double engineTemperatureCelsius,
        double trafficDelayMinutes
) {}
