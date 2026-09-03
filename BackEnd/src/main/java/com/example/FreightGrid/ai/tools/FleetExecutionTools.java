package com.example.FreightGrid.ai.tools;

import com.example.FreightGrid.domain.entity.ShipmentEntity;
import com.example.FreightGrid.domain.entity.VehicleEntity;
import com.example.FreightGrid.domain.enums.VehicleStatus;
import com.example.FreightGrid.domain.repository.IncidentLogRepository;
import com.example.FreightGrid.domain.repository.ShipmentRepository;
import com.example.FreightGrid.domain.repository.VehicleRepository;
import dev.langchain4j.agent.tool.Tool;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Execution-level fleet tools for the multi-agent swarm.
 *
 * <p>Unlike {@link FleetTools} (read-only queries) and {@link FinancialTools} (cost calculations),
 * this component performs <b>write operations</b> against the Neon DB — vehicle swaps,
 * status transitions, and shipment reassignments.</p>
 */
@Component
//@RequiredArgsConstructor
@Slf4j
@Builder
//@NoArgsConstructor
//@AllArgsConstructor
public class FleetExecutionTools {

    private final ShipmentRepository shipmentRepository;
    private final VehicleRepository vehicleRepository;
    private final IncidentLogRepository incidentLogRepository;

    public FleetExecutionTools(ShipmentRepository shipmentRepository, VehicleRepository vehicleRepository, IncidentLogRepository incidentLogRepository) {
        this.shipmentRepository = shipmentRepository;
        this.vehicleRepository = vehicleRepository;
        this.incidentLogRepository = incidentLogRepository;
    }

    /**
     * Executes a real-time vehicle swap for an active shipment.
     *
     * <p>Workflow:
     * <ol>
     *   <li>Fetch the shipment by tracking number.</li>
     *   <li>Mark the shipment's current (broken) vehicle as {@code BROKEN}.</li>
     *   <li>Mark the rescue vehicle as {@code IN_TRANSIT}.</li>
     *   <li>Reassign the shipment to the rescue vehicle.</li>
     *   <li>Persist all changes to Neon DB.</li>
     * </ol>
     * </p>
     *
     * @param trackingNumber     the shipment tracking number (e.g., "TRK-0042")
     * @param rescueVehicleCode  the vehicle code of the rescue vehicle (e.g., "VH-007")
     * @param newRouteDetails    human-readable description of the new route
     * @return confirmation string summarising the swap
     */
    @Tool("Execute real-time vehicle swap and re-route for a shipment in database")
    @Transactional
    public String executeVehicleSwap(String trackingNumber, String rescueVehicleCode, String newRouteDetails) {
        log.info("⚡ Executing vehicle swap: shipment={}, rescue={}", trackingNumber, rescueVehicleCode);

        // 1. Fetch shipment
        ShipmentEntity shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Shipment not found: " + trackingNumber));

        // 2. Fetch broken vehicle (the one currently assigned to the shipment)
        VehicleEntity brokenVehicle = shipment.getVehicle();

        // 3. Fetch rescue vehicle
        VehicleEntity rescueVehicle = vehicleRepository.findByVehicleCode(rescueVehicleCode)
                .orElseThrow(() -> new RuntimeException("Rescue vehicle not found: " + rescueVehicleCode));

        // 4. Update statuses
        brokenVehicle.setStatus(VehicleStatus.BROKEN);
        vehicleRepository.save(brokenVehicle);

        rescueVehicle.setStatus(VehicleStatus.IN_TRANSIT);
        vehicleRepository.save(rescueVehicle);

        // 5. Reassign shipment to rescue vehicle
        shipment.setVehicle(rescueVehicle);
        shipmentRepository.save(shipment);

        String confirmation = String.format(
                "SWAP EXECUTED — Shipment %s reassigned from %s (now BROKEN) to %s (now IN_TRANSIT). New route: %s",
                trackingNumber,
                brokenVehicle.getVehicleCode(),
                rescueVehicleCode,
                newRouteDetails
        );
        log.info("✔ {}", confirmation);
        return confirmation;
    }

    @Tool("Add a newly purchased or leased vehicle to the FreightGrid Database")
    public String addVehicle(String vehicleCode,double costPerMile){
        VehicleEntity vehicle=VehicleEntity.builder()
                .vehicleCode(vehicleCode)
                .costPerMile(BigDecimal.valueOf(costPerMile))
                .driverHosRemaining(11.0)
                .status(VehicleStatus.EMPTY)
                .build();

        vehicleRepository.save(vehicle);
        return "Successfully added "+vehicleCode+"to the fleet";
    }
}
