package com.example.FreightGrid.web.controller;

import com.example.FreightGrid.domain.entity.VehicleEntity;
import com.example.FreightGrid.domain.repository.VehicleRepository;
import com.example.FreightGrid.web.dto.CreateVehicleDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    private final VehicleRepository vehicleRepository;

    public VehicleController(VehicleRepository vehicleRepository){
        this.vehicleRepository=vehicleRepository;
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleEntity> createVehicleDto(@Valid @RequestBody CreateVehicleDto vehicleDto){
        VehicleEntity vehicle =new VehicleEntity();

        vehicle.setVehicleCode(vehicleDto.vehicleId());
        vehicle.setCostPerMile(vehicleDto.costPerMile());
        vehicle.setDriverHosRemaining(vehicleDto.driverHosRemaining());
        vehicle.setStatus(vehicleDto.status());

        vehicle.setCurrentNode(null);


        VehicleEntity vehicleEntity=vehicleRepository.save(vehicle);

        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleEntity);
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<VehicleEntity>> getVehicles(){
            List<VehicleEntity> vehicle=vehicleRepository.findAll();
            return ResponseEntity.ok(vehicle);
    }


}
