package com.example.FreightGrid.domain.repository;

import com.example.FreightGrid.domain.entity.VehicleEntity;
import com.example.FreightGrid.domain.enums.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {

    Optional<VehicleEntity> findByVehicleCode(String vehicleCode);
    List<VehicleEntity> findByStatus(VehicleStatus status);
}
