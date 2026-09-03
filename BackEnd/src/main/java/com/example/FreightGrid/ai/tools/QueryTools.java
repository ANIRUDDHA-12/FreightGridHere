package com.example.FreightGrid.ai.tools;

import com.example.FreightGrid.domain.entity.ShipmentEntity;
import com.example.FreightGrid.domain.entity.VehicleEntity;
import com.example.FreightGrid.domain.repository.ShipmentRepository;
import com.example.FreightGrid.domain.repository.VehicleRepository;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Read-only query tools for the FleetChatbotAgent.
 *
 * <p><b>CRITICAL:</b> These methods perform ONLY read operations.
 * No database saves, updates, or deletes are permitted here.</p>
 */
@Component
@RequiredArgsConstructor
public class QueryTools {

    private final VehicleRepository vehicleRepository;
    private final ShipmentRepository shipmentRepository;

    /**
     * Finds the current location, status, and HOS of a specific vehicle.
     *
     * @param vehicleCode the vehicle code (e.g., "FG-TRK-001")
     * @return structured text summary of vehicle status
     */
    @Tool("Find current location, status, and HOS of a specific vehicle")
    public String getVehicleStatus(String vehicleCode) {
        VehicleEntity vehicle = vehicleRepository.findByVehicleCode(vehicleCode)
                .orElse(null);

        if (vehicle == null) {
            return "Vehicle not found: " + vehicleCode;
        }

        String nodeName = (vehicle.getCurrentNode() != null)
                ? vehicle.getCurrentNode().getName()
                : "UNKNOWN";

        return String.format(
                "Vehicle %s:\n- Status: %s\n- Location: %s\n- Driver HOS Remaining: %.1fh\n- Cost Per Mile: $%s",
                vehicle.getVehicleCode(),
                vehicle.getStatus(),
                nodeName,
                vehicle.getDriverHosRemaining(),
                vehicle.getCostPerMile().toPlainString()
        );
    }

    /**
     * Finds the SLA deadline, cargo value, and penalty details for a specific shipment.
     *
     * @param trackingNumber the shipment tracking number (e.g., "FG-SHP-20240001")
     * @return structured text summary of shipment status
     */
    @Tool("Find SLA deadline and cargo value for a specific shipment")
    public String getShipmentStatus(String trackingNumber) {
        ShipmentEntity shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElse(null);

        if (shipment == null) {
            return "Shipment not found: " + trackingNumber;
        }

        String vehicleInfo = (shipment.getVehicle() != null)
                ? shipment.getVehicle().getVehicleCode()
                : "UNASSIGNED";

        return String.format(
                "Shipment %s:\n- Assigned Vehicle: %s\n- Cargo Value: $%s\n- Max Temp Limit: %s\n- SLA Deadline (UTC): %s\n- Penalty Per Hour: $%s",
                shipment.getTrackingNumber(),
                vehicleInfo,
                shipment.getCargoValue().toPlainString(),
                shipment.getMaxTempLimit() != null ? shipment.getMaxTempLimit() + "°C" : "N/A (ambient)",
                shipment.getSlaDeadline(),
                shipment.getPenaltyPerHour().toPlainString()
        );
    }
}
