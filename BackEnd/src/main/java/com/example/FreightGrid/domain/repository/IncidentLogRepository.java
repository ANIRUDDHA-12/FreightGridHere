package com.example.FreightGrid.domain.repository;

import com.example.FreightGrid.domain.entity.IncidentLogEntity;
import com.example.FreightGrid.domain.entity.ShipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentLogRepository extends JpaRepository<IncidentLogEntity, Long> {

    List<IncidentLogEntity> findAllByShipment(ShipmentEntity shipment);
}
