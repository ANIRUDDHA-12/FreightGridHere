package com.example.FreightGrid.service;

import com.example.FreightGrid.ai.agent.DispatcherAgent;
import com.example.FreightGrid.ai.agent.FinancialAgent;
import com.example.FreightGrid.ai.agent.PostMortemAgent;
import com.example.FreightGrid.ai.agent.QualityAssuranceAgent;
import com.example.FreightGrid.ai.dto.QAResponse;
import com.example.FreightGrid.ai.dto.RcaResultDTO;
import com.example.FreightGrid.domain.entity.IncidentLogEntity;
import com.example.FreightGrid.domain.entity.ShipmentEntity;
import com.example.FreightGrid.domain.enums.IncidentCategory;
import com.example.FreightGrid.domain.repository.IncidentLogRepository;
import com.example.FreightGrid.domain.repository.ShipmentRepository;
import com.example.FreightGrid.web.dto.SimulateIncidentRequestDTO;
import com.example.FreightGrid.web.dto.SimulateIncidentResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IncidentOrchestratorServiceTest {

    @Mock
    private SimulateIncidentResponseDTO simulateIncidentResponseDTO;

    @Mock
    private DispatcherAgent dispatcherAgent;

    @Mock
    private FinancialAgent financialAgent;

    @Mock
    private QualityAssuranceAgent qualityAssuranceAgent;

    @Mock
    private PostMortemAgent postMortemAgent;

    @Mock
    private IncidentLogRepository incidentLogRepository;

    @Mock
    private ShipmentRepository shipmentRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private IncidentOrchestratorService orchestratorService;

    @Test
    public void testResolveIncident_QaFailsThenSucceeds() {
        SimulateIncidentRequestDTO request = new SimulateIncidentRequestDTO("TRK-123", "Route blocked", 2.0,"GPS_OK");
        ShipmentEntity shipment = new ShipmentEntity();

        when(shipmentRepository.findByTrackingNumber(anyString())).thenReturn(Optional.of(shipment));

        when(dispatcherAgent.findRescueRoutes(anyString())).thenReturn("Plan 1", "Plan 2");
        when(financialAgent.evaluateCosts(anyString())).thenReturn("Cost 1", "Cost 2");

        when(qualityAssuranceAgent.validateStrategy(anyString()))
                .thenReturn(new QAResponse(false, "Too expensive"))
                .thenReturn(new QAResponse(true, "Looks good"));

        when(postMortemAgent.generateReport(anyString()))
                .thenReturn(new RcaResultDTO(IncidentCategory.TRAFFIC, "## RCA Report\nTraffic incident.", false));

        SimulateIncidentResponseDTO response = orchestratorService.resolveIncident(request);

        assertTrue(response.approved());
        assertEquals(2, response.retriesUsed());

        verify(dispatcherAgent, times(2)).findRescueRoutes(anyString());
        verify(qualityAssuranceAgent, times(2)).validateStrategy(anyString());
        verify(postMortemAgent, times(1)).generateReport(anyString());
        verify(incidentLogRepository, times(1)).save(any(IncidentLogEntity.class));
    }

    @Test
    public void testResolveIncident_AllRetriesFail() {
        SimulateIncidentRequestDTO request = new SimulateIncidentRequestDTO("TRK-456", "Engine failure", 3.0,"GPS_WORKING");
        ShipmentEntity shipment = new ShipmentEntity();

        when(shipmentRepository.findByTrackingNumber(anyString())).thenReturn(Optional.of(shipment));
        when(dispatcherAgent.findRescueRoutes(anyString())).thenReturn("Plan");
        when(financialAgent.evaluateCosts(anyString())).thenReturn("Cost");
        when(qualityAssuranceAgent.validateStrategy(anyString()))
                .thenReturn(new QAResponse(false, "Not viable"));

        SimulateIncidentResponseDTO response = orchestratorService.resolveIncident(request);

        assertFalse(response.approved());
        assertEquals(3, response.retriesUsed());
        assertEquals("All retries failed", response.finalStrategy());

        verify(dispatcherAgent, times(3)).findRescueRoutes(anyString());
        verify(postMortemAgent, never()).generateReport(anyString());
        verify(incidentLogRepository, times(1)).save(any(IncidentLogEntity.class));
    }
}
