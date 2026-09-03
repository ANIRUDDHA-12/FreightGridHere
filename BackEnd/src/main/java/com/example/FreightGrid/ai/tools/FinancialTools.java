package com.example.FreightGrid.ai.tools;

import com.example.FreightGrid.domain.entity.ShipmentEntity;
import com.example.FreightGrid.domain.entity.VehicleEntity;
import com.example.FreightGrid.domain.repository.ShipmentRepository;
import com.example.FreightGrid.domain.repository.VehicleRepository;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Financial calculation tools for the multi-agent swarm.
 * Provides cost estimation and SLA penalty capabilities to the FinancialAgent
 * for evaluating dispatch proposals against real entity data.
 */
@Component
@RequiredArgsConstructor
public class FinancialTools {

    private final VehicleRepository vehicleRepository;
    private final ShipmentRepository shipmentRepository;

    /**
     * Calculates the financial cost of dispatching a specific vehicle
     * over a given distance using the vehicle's actual per-mile rate.
     *
     * @param vehicleCode   the unique code identifying the vehicle (e.g., "VH-001")
     * @param distanceMiles the distance in miles the vehicle would travel
     * @return the estimated cost in USD
     */
    @Tool("Calculate financial cost of dispatching a specific vehicle for mitigation")
    public double calculateMitigationCost(String vehicleCode, double distanceMiles) {
        VehicleEntity vehicle = vehicleRepository.findByVehicleCode(vehicleCode)
                .orElseThrow(() -> new RuntimeException("Vehicle not found: " + vehicleCode));
        return vehicle.getCostPerMile().doubleValue() * distanceMiles;
    }

    /**
     * Calculates SLA breach penalty exposure for a specific shipment
     * based on its contractual penalty-per-hour rate.
     *
     * @param trackingNumber      the shipment tracking number (e.g., "TRK-0042")
     * @param estimatedDelayHours the estimated delay in hours
     * @return formatted summary including cargo value and calculated penalty
     */
    @Tool("Calculate SLA breach penalty exposure for a shipment")
    public String calculatePenaltyExposure(String trackingNumber, double estimatedDelayHours) {
        ShipmentEntity shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Shipment not found: " + trackingNumber));

        double penalty = shipment.getPenaltyPerHour().doubleValue() * estimatedDelayHours;
        return String.format(
                "Shipment %s: cargo=$%,.2f, penalty=$%,.2f for %.1fh delay",
                trackingNumber, shipment.getCargoValue(), penalty, estimatedDelayHours
        );
    }
}
