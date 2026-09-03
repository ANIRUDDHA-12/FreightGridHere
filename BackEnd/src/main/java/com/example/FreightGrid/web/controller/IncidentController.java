package com.example.FreightGrid.web.controller;

import com.example.FreightGrid.service.IncidentOrchestratorService;
import com.example.FreightGrid.web.dto.SimulateIncidentRequestDTO;
import com.example.FreightGrid.web.dto.SimulateIncidentResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentOrchestratorService orchestratorService;

    @PostMapping(value = "/simulate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimulateIncidentResponseDTO> simulateIncident(@Valid @RequestBody SimulateIncidentRequestDTO request) {
        SimulateIncidentResponseDTO response = orchestratorService.resolveIncident(request);
        return ResponseEntity.ok(response);
    }
}
