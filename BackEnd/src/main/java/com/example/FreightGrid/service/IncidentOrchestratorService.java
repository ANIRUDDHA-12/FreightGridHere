package com.example.FreightGrid.service;

import com.example.FreightGrid.ai.agent.DispatcherAgent;
import com.example.FreightGrid.ai.agent.FinancialAgent;
import com.example.FreightGrid.ai.agent.PostMortemAgent;
import com.example.FreightGrid.ai.agent.QualityAssuranceAgent;
import com.example.FreightGrid.ai.tools.FleetExecutionTools;
import com.example.FreightGrid.ai.dto.QAResponse;
import com.example.FreightGrid.ai.dto.RcaContextDTO;
import com.example.FreightGrid.ai.dto.RcaResultDTO;
import com.example.FreightGrid.domain.entity.IncidentLogEntity;
import com.example.FreightGrid.domain.entity.ShipmentEntity;
import com.example.FreightGrid.domain.enums.IncidentStatus;
import com.example.FreightGrid.domain.repository.IncidentLogRepository;
import com.example.FreightGrid.domain.repository.ShipmentRepository;
import com.example.FreightGrid.web.dto.SimulateIncidentRequestDTO;
import com.example.FreightGrid.web.dto.SimulateIncidentResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class IncidentOrchestratorService {

    private final DispatcherAgent dispatcherAgent;
    private final FinancialAgent financialAgent;
    private final QualityAssuranceAgent qualityAssuranceAgent;
    private final PostMortemAgent postMortemAgent;
    private final FleetExecutionTools fleetExecutionTools;
    private final IncidentLogRepository incidentLogRepository;
    private final ShipmentRepository shipmentRepository;
    private final ObjectMapper objectMapper;

    public SimulateIncidentResponseDTO resolveIncident(SimulateIncidentRequestDTO request) {
        int maxRetries = 3;
        String feedback = "";

        ShipmentEntity shipment = shipmentRepository.findByTrackingNumber(request.trackingNumber())
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        for (int i = 0; i < maxRetries; i++) {
            String prompt = request.disruptionType() + " " + feedback;
            String dispatchPlan = dispatcherAgent.findRescueRoutes(prompt);
            String financialPlan = financialAgent.evaluateCosts(dispatchPlan);
            QAResponse qaResult = qualityAssuranceAgent.validateStrategy(financialPlan);

            if (qaResult.isApproved()) {
                double estimatedCost = extractCost(financialPlan);
                boolean requiresDirectorApproval = estimatedCost > 5000.0;

                // ── Execute Vehicle Swap ─────────────────────────────────────
                String rescueVehicleCode = extractRescueVehicleCode(dispatchPlan);
                if (rescueVehicleCode != null) {
                    try {
                        fleetExecutionTools.executeVehicleSwap(request.trackingNumber(), rescueVehicleCode, dispatchPlan);
                    } catch (Exception e) {
                        log.warn("Failed to execute vehicle swap", e);
                    }
                }

                // ── Post-Mortem RCA ──────────────────────────────────────────
                RcaContextDTO rcaContext = new RcaContextDTO(
                        request.trackingNumber(),
                        request.disruptionType(),
                        estimatedCost,
                        qaResult.getReasoning(),
                        request.telemetryData()
                );

                RcaResultDTO rca = null;
                try {
                    String contextJson = objectMapper.writeValueAsString(rcaContext);
                    rca = postMortemAgent.generateReport(contextJson);
                    log.info("✔ RCA complete: category={}, flagForReview={}",
                            rca.incidentCategory(), rca.flagForReview());
                } catch (JsonProcessingException e) {
                    log.warn("Failed to serialize RCA context — skipping post-mortem", e);
                } catch (Exception e) {
                    log.warn("PostMortemAgent failed — incident will be saved without RCA", e);
                }

                // ── Build and persist IncidentLog ────────────────────────────
                String strategies = financialPlan;
                if (rca != null) {
                    strategies = String.format("[%s] %s\n\n---\n\n%s",
                            rca.incidentCategory(),
                            rca.flagForReview() ? "[URGENT HR/OPS REVIEW REQUIRED]" : "",
                            rca.markdownReport()
                    );
                }

                IncidentLogEntity logEntity = IncidentLogEntity.builder()
                        .shipment(shipment)
                        .disruptionType(request.disruptionType())
                        .aiProposedStrategies(strategies)
                        .selectedStrategy(qaResult.getReasoning())
                        .status(IncidentStatus.PENDING_APPROVAL)
                        .category(rca != null ? rca.incidentCategory() : null)
                        .flaggedForReview(rca != null && rca.flagForReview())
                        .build();
                incidentLogRepository.save(logEntity);

                return new SimulateIncidentResponseDTO(
                        request.trackingNumber(),
                        qaResult.getReasoning(),
                        true,
                        i + 1,
                        requiresDirectorApproval,
                        estimatedCost
                );
            } else {
                feedback = "Previous attempt rejected: " + qaResult.getReasoning();
            }
        }

        IncidentLogEntity logEntity = IncidentLogEntity.builder()
                .shipment(shipment)
                .disruptionType(request.disruptionType())
                .status(IncidentStatus.REJECTED)
                .build();
        incidentLogRepository.save(logEntity);

        return new SimulateIncidentResponseDTO(
                request.trackingNumber(),
                "All retries failed",
                false,
                maxRetries,
                false,
                0.0
        );
    }

    private double extractCost(String text) {
        try {
            Pattern pattern = Pattern.compile("\\$([0-9,]+(\\.[0-9]{2})?)");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String costStr = matcher.group(1).replace(",", "");
                return Double.parseDouble(costStr);
            }
        } catch (Exception e) {
            log.warn("Failed to extract cost from financial plan", e);
        }
        return 0.0;
    }

    private String extractRescueVehicleCode(String text) {
        try {
            Pattern pattern = Pattern.compile("(FG-TRK-\\d{3})");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            log.warn("Failed to extract rescue vehicle code", e);
        }
        return null;
    }
}
