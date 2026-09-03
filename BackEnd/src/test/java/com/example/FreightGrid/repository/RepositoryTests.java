package com.example.FreightGrid.repository;

import com.example.FreightGrid.domain.entity.IncidentLogEntity;
import com.example.FreightGrid.domain.entity.ShipmentEntity;
import com.example.FreightGrid.domain.entity.VehicleEntity;
import com.example.FreightGrid.domain.enums.IncidentStatus;
import com.example.FreightGrid.domain.enums.VehicleStatus;
import com.example.FreightGrid.domain.repository.IncidentLogRepository;
import com.example.FreightGrid.domain.repository.ShipmentRepository;
import com.example.FreightGrid.domain.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class RepositoryTests {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private IncidentLogRepository incidentLogRepository;

    @Test
    void testFindByTrackingNumber() {
        // Arrange
        VehicleEntity vehicle = new VehicleEntity();
        vehicle.setVehicleCode("TEST-VEH-01");
        vehicle.setCostPerMile(new BigDecimal("2.50"));
        vehicle.setStatus(VehicleStatus.IN_TRANSIT);
        vehicle.setDriverHosRemaining(8.5);
        vehicleRepository.save(vehicle);

        ShipmentEntity shipment = new ShipmentEntity();
        shipment.setTrackingNumber("TRK-123456");
        shipment.setCargoValue(new BigDecimal("10000.00"));
        shipment.setSlaDeadline(Instant.now());
        shipment.setPenaltyPerHour(new BigDecimal("100.00"));
        shipment.setVehicle(vehicle);
        shipmentRepository.save(shipment);

        // Act
        Optional<ShipmentEntity> found = shipmentRepository.findByTrackingNumber("TRK-123456");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getTrackingNumber()).isEqualTo("TRK-123456");
    }

    @Test
    void testFindByVehicleCode() {
        // Arrange
        VehicleEntity vehicle = new VehicleEntity();
        vehicle.setVehicleCode("TRK-999");
        vehicle.setCostPerMile(new BigDecimal("3.00"));
        vehicle.setStatus(VehicleStatus.IN_TRANSIT);
        vehicle.setDriverHosRemaining(5.0);
        vehicleRepository.save(vehicle);

        // Act
        Optional<VehicleEntity> found = vehicleRepository.findByVehicleCode("TRK-999");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getVehicleCode()).isEqualTo("TRK-999");
    }

    @Test
    void testFindAllByShipment() {
        // Arrange
        VehicleEntity vehicle = new VehicleEntity();
        vehicle.setVehicleCode("TEST-VEH-02");
        vehicle.setCostPerMile(new BigDecimal("2.50"));
        vehicle.setStatus(VehicleStatus.IN_TRANSIT);
        vehicle.setDriverHosRemaining(6.0);
        vehicleRepository.save(vehicle);

        ShipmentEntity shipment = new ShipmentEntity();
        shipment.setTrackingNumber("TRK-789012");
        shipment.setCargoValue(new BigDecimal("20000.00"));
        shipment.setSlaDeadline(Instant.now());
        shipment.setPenaltyPerHour(new BigDecimal("200.00"));
        shipment.setVehicle(vehicle);
        shipmentRepository.save(shipment);

        IncidentLogEntity log1 = new IncidentLogEntity();
        log1.setShipment(shipment);
        log1.setDisruptionType("Test Incident 1");
        log1.setStatus(IncidentStatus.PENDING_APPROVAL);
        incidentLogRepository.save(log1);

        IncidentLogEntity log2 = new IncidentLogEntity();
        log2.setShipment(shipment);
        log2.setDisruptionType("Test Incident 2");
        log2.setStatus(IncidentStatus.PENDING_APPROVAL);
        incidentLogRepository.save(log2);

        // Act
        List<IncidentLogEntity> logs = incidentLogRepository.findAllByShipment(shipment);

        // Assert
        assertThat(logs).hasSize(2);
    }
}
