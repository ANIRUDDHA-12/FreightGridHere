package com.example.FreightGrid.web.controller;

import com.example.FreightGrid.domain.entity.ShipmentEntity;
import com.example.FreightGrid.domain.repository.ShipmentRepository;
import com.example.FreightGrid.service.IncidentOrchestratorService;
import com.example.FreightGrid.web.dto.SimulateIncidentRequestDTO;
import com.example.FreightGrid.web.dto.SimulateIncidentResponseDTO;
import com.example.FreightGrid.web.dto.TelemetryPingDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Receives real-time telemetry pings from vehicle IoT/GPS units.
 *
 * <p>Evaluates each ping against predictive alert thresholds. When a threshold
 * is breached, the controller automatically triggers the multi-agent incident
 * resolution swarm — converting raw sensor data into autonomous re-routing.</p>
 */
@RestController
@RequestMapping("/api/v1/telemetry")
@RequiredArgsConstructor
@Slf4j
public class TelemetryController {

    private final IncidentOrchestratorService orchestratorService;
    private final ShipmentRepository shipmentRepository;

    /**
     * Ingests a single telemetry ping and evaluates predictive alert thresholds.
     *
     * <p>Alert triggers:
     * <ul>
     *   <li>Engine temperature &gt; 105.0 °C (mechanical failure risk)</li>
     *   <li>Speed &lt; 15 mph AND traffic delay &gt; 45 min (severe congestion / SLA risk)</li>
     * </ul>
     * </p>
     *
     * @param ping the telemetry data from the vehicle
     * @return alert status message
     */
    @PostMapping(value = "/ping", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> receivePing(@RequestBody TelemetryPingDTO ping) {
        log.info("📡 Telemetry ping received: vehicle={}, speed={}mph, engineTemp={}°C, trafficDelay={}min",
                ping.vehicleCode(), ping.currentSpeedMph(),
                ping.engineTemperatureCelsius(), ping.trafficDelayMinutes());

        boolean engineOverheat = ping.engineTemperatureCelsius() > 105.0;
        boolean severeCongestion = ping.currentSpeedMph() < 15.0 && ping.trafficDelayMinutes() > 45.0;

        if (engineOverheat || severeCongestion) {
            log.warn("🚨 Predictive alert threshold breached for vehicle {}", ping.vehicleCode());

            // Find the active shipment on this vehicle
            ShipmentEntity shipment = shipmentRepository.findByVehicle_VehicleCode(ping.vehicleCode())
                    .orElse(null);

            if (shipment == null) {
                log.warn("No active shipment found for vehicle {}, skipping swarm trigger", ping.vehicleCode());
                return ResponseEntity.ok("TELEMETRY ALERT — no active shipment on vehicle " + ping.vehicleCode());
            }

            // Determine disruption type from telemetry
            String disruptionType = engineOverheat
                    ? String.format("ENGINE OVERHEAT: Vehicle %s engine at %.1f°C — mechanical failure imminent",
                            ping.vehicleCode(), ping.engineTemperatureCelsius())
                    : String.format("SEVERE CONGESTION: Vehicle %s speed %.1fmph with %.0fmin traffic delay — SLA breach risk",
                            ping.vehicleCode(), ping.currentSpeedMph(), ping.trafficDelayMinutes());

            // Estimate delay based on telemetry
            double estimatedDelayHours = engineOverheat ? 4.0 : (ping.trafficDelayMinutes() / 60.0);

            // Serialize ping to pass as telemetryData
            String telemetryData = String.format("{\"speed\":%f,\"temp\":%f,\"delay\":%f}", 
                    ping.currentSpeedMph(), ping.engineTemperatureCelsius(), ping.trafficDelayMinutes());

            SimulateIncidentRequestDTO request = new SimulateIncidentRequestDTO(
                    shipment.getTrackingNumber(),
                    disruptionType,
                    estimatedDelayHours,
                    telemetryData
            );

            SimulateIncidentResponseDTO result = orchestratorService.resolveIncident(request);
            log.info("✔ Swarm resolution complete for {}: approved={}", ping.vehicleCode(), result.approved());

            return ResponseEntity.ok("PREDICTIVE ALERT TRIGGERED: Swarm initiated re-route.");
        }

        return ResponseEntity.ok("TELEMETRY NORMAL");
    }
}
