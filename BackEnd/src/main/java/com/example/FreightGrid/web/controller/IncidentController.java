package com.example.FreightGrid.web.controller;

import com.example.FreightGrid.domain.entity.IncidentLogEntity;
import com.example.FreightGrid.domain.repository.IncidentLogRepository;
import com.example.FreightGrid.service.IncidentOrchestratorService;
import com.example.FreightGrid.web.dto.SimulateIncidentRequestDTO;
import com.example.FreightGrid.web.dto.SimulateIncidentResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentOrchestratorService orchestratorService;
    private final IncidentLogRepository incidentLogRepository;

    @PostMapping(value = "/simulate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimulateIncidentResponseDTO> simulateIncident(@Valid @RequestBody SimulateIncidentRequestDTO request) {
        SimulateIncidentResponseDTO response = orchestratorService.resolveIncident(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value="/active",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<IncidentLogEntity>> getActiveIncidents(){
        List<IncidentLogEntity> incidents=incidentLogRepository.findAll();
        return ResponseEntity.ok(incidents);
    }
}
