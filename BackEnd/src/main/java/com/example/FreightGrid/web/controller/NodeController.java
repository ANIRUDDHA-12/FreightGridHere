package com.example.FreightGrid.web.controller;

import com.example.FreightGrid.domain.entity.NodeEntity;
import com.example.FreightGrid.domain.repository.NodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/nodes")
@RequiredArgsConstructor
public class NodeController {
    private final NodeRepository nodeRepository;

    @GetMapping(value="/list",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NodeEntity>> getActiveNodes(){
        List<NodeEntity> response=nodeRepository.findAll();
        return ResponseEntity.ok(response);
    }
}
