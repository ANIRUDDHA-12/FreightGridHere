package com.example.FreightGrid.config;

import com.example.FreightGrid.domain.entity.NodeEntity;
import com.example.FreightGrid.domain.entity.ShipmentEntity;
import com.example.FreightGrid.domain.entity.VehicleEntity;
import com.example.FreightGrid.domain.enums.NodeType;
import com.example.FreightGrid.domain.enums.VehicleStatus;
import com.example.FreightGrid.domain.repository.NodeRepository;
import com.example.FreightGrid.domain.repository.ShipmentRepository;
import com.example.FreightGrid.domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * DataSeeder seeds the Neon DB with representative freight network data on
 * first startup. Guards against duplicate inserts by checking node count == 0.
 *
 * Seed dataset:
 *   - 4 Nodes  (Warehouse × 2, Distribution Hub × 1, Client Factory × 1)
 *   - 6 Vehicles (mix of statuses; active trucks with HOS 1.5 – 8.5 h)
 *   - 2 active Shipments (realistic financials per Sprint 1 spec)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final NodeRepository       nodeRepository;
    private final VehicleRepository    vehicleRepository;
    private final ShipmentRepository   shipmentRepository;

    @Override
    public void run(String... args) {
        if (nodeRepository.count() > 0) {
            log.info("[DataSeeder] Database already seeded – skipping.");
            return;
        }

        log.info("[DataSeeder] Starting seed of FreightGrid mock data...");

        // ── 1. NODES ──────────────────────────────────────────────────────────
        NodeEntity chicago = nodeRepository.save(NodeEntity.builder()
                .name("Chicago Central Warehouse")
                .type(NodeType.WAREHOUSE)
                .latitude(41.8781)
                .longitude(-87.6298)
                .capacity(500)
                .build());

        NodeEntity dallas = nodeRepository.save(NodeEntity.builder()
                .name("Dallas South Warehouse")
                .type(NodeType.WAREHOUSE)
                .latitude(32.7767)
                .longitude(-96.7970)
                .capacity(350)
                .build());

        NodeEntity atlanta = nodeRepository.save(NodeEntity.builder()
                .name("Atlanta Distribution Hub")
                .type(NodeType.DISTRIBUTION_HUB)
                .latitude(33.7490)
                .longitude(-84.3880)
                .capacity(800)
                .build());

        NodeEntity detroit = nodeRepository.save(NodeEntity.builder()
                .name("Detroit Client Factory")
                .type(NodeType.CLIENT_FACTORY)
                .latitude(42.3314)
                .longitude(-83.0458)
                .capacity(200)
                .build());

        log.info("[DataSeeder] ✓ 4 Nodes saved.");

        // ── 2. VEHICLES ───────────────────────────────────────────────────────
        // Active trucks: HOS between 1.5 and 8.5 hours (FMCSA 11-hour rule)
        VehicleEntity v1 = vehicleRepository.save(VehicleEntity.builder()
                .vehicleCode("FG-TRK-001")
                .status(VehicleStatus.IN_TRANSIT)
                .currentNode(chicago)
                .driverHosRemaining(8.5)
                .costPerMile(new BigDecimal("3.25"))
                .build());

        VehicleEntity v2 = vehicleRepository.save(VehicleEntity.builder()
                .vehicleCode("FG-TRK-002")
                .status(VehicleStatus.IN_TRANSIT)
                .currentNode(atlanta)
                .driverHosRemaining(5.0)
                .costPerMile(new BigDecimal("2.75"))
                .build());

        vehicleRepository.save(VehicleEntity.builder()
                .vehicleCode("FG-TRK-003")
                .status(VehicleStatus.EMPTY)
                .currentNode(dallas)
                .driverHosRemaining(4.5)
                .costPerMile(new BigDecimal("3.00"))
                .build());

        vehicleRepository.save(VehicleEntity.builder()
                .vehicleCode("FG-TRK-004")
                .status(VehicleStatus.EMPTY)
                .currentNode(atlanta)
                .driverHosRemaining(1.5)
                .costPerMile(new BigDecimal("4.50"))
                .build());

        vehicleRepository.save(VehicleEntity.builder()
                .vehicleCode("FG-TRK-005")
                .status(VehicleStatus.MAINTENANCE)
                .currentNode(chicago)
                .driverHosRemaining(11.0)   // fresh HOS – back from rest
                .costPerMile(new BigDecimal("2.50"))
                .build());

        vehicleRepository.save(VehicleEntity.builder()
                .vehicleCode("FG-TRK-006")
                .status(VehicleStatus.BROKEN)
                .currentNode(detroit)
                .driverHosRemaining(3.0)
                .costPerMile(new BigDecimal("3.75"))
                .build());

        log.info("[DataSeeder] ✓ 6 Vehicles saved.");

        // ── 3. SHIPMENTS ──────────────────────────────────────────────────────
        // SLA deadlines: 24h and 48h from now (realistic active shipment window)
        Instant now = Instant.now();

        shipmentRepository.save(ShipmentEntity.builder()
                .trackingNumber("FG-SHP-20240001")
                .vehicle(v1)
                .cargoValue(new BigDecimal("125000.00"))
                .maxTempLimit(-18.0)            // frozen goods
                .slaDeadline(now.plus(24, ChronoUnit.HOURS))
                .penaltyPerHour(new BigDecimal("1500.00"))
                .build());

        shipmentRepository.save(ShipmentEntity.builder()
                .trackingNumber("FG-SHP-20240002")
                .vehicle(v2)
                .cargoValue(new BigDecimal("75000.00"))
                .maxTempLimit(null)             // ambient cargo
                .slaDeadline(now.plus(48, ChronoUnit.HOURS))
                .penaltyPerHour(new BigDecimal("750.00"))
                .build());

        log.info("[DataSeeder] ✓ 2 active Shipments saved.");
        log.info("[DataSeeder] ─── Seed complete. FreightGrid is ready. ───");
    }
}
