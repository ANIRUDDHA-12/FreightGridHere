package com.example.FreightGrid.ai.tools;

import com.example.FreightGrid.domain.entity.VehicleEntity;
import com.example.FreightGrid.domain.enums.VehicleStatus;
import com.example.FreightGrid.domain.repository.VehicleRepository;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Fleet query tools for the multi-agent swarm.
 * Provides the DispatcherAgent with enriched vehicle data
 * so it can make informed rescue vehicle recommendations.
 */
@Component
@RequiredArgsConstructor
public class FleetTools {

    private final VehicleRepository vehicleRepository;

    /**
     * Finds all available (EMPTY status) rescue vehicles and returns a structured
     * text summary with location, driver HOS, and cost-per-mile for each vehicle.
     * This gives the LLM enough context to recommend the best candidate.
     *
     * @return formatted multi-line summary of each available vehicle
     */
    @Tool("Find available rescue vehicles with location and HOS details")
    public String findRescueVehiclesWithDetails() {
        List<VehicleEntity> emptyVehicles = vehicleRepository.findByStatus(VehicleStatus.EMPTY);

        if (emptyVehicles.isEmpty()) {
            return "NO RESCUE VEHICLES AVAILABLE — all vehicles are currently assigned or in maintenance.";
        }

        StringBuilder sb = new StringBuilder("Available rescue vehicles:\n");
        for (VehicleEntity v : emptyVehicles) {
            String nodeName = (v.getCurrentNode() != null) ? v.getCurrentNode().getName() : "UNKNOWN";
            sb.append(String.format(
                    "- %s | Location: %s | HOS Remaining: %.1fh | Cost: $%s/mile%n",
                    v.getVehicleCode(),
                    nodeName,
                    v.getDriverHosRemaining(),
                    v.getCostPerMile().toPlainString()
            ));
        }
        return sb.toString();
    }
}
