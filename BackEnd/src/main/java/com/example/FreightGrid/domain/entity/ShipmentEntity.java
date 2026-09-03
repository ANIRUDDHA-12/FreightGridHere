package com.example.FreightGrid.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "shipments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique tracking number for customer-facing shipment identification.
     * Enforced at DB level via unique index.
     */
    @Column(nullable = false, unique = true)
    private String trackingNumber;

    /**
     * The vehicle currently carrying this shipment.
     * ManyToOne – one vehicle can carry multiple shipments (LTL scenarios).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private VehicleEntity vehicle;

    /** Declared cargo value. Stored with scale 2. Range: $30,000 – $200,000. */
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal cargoValue;

    /** Maximum allowed temperature in °C for refrigerated cargo. */
    @Column(nullable = true)
    private Double maxTempLimit;

    /** SLA deadline – UTC instant by which the shipment must be delivered. */
    @Column(nullable = false)
    private Instant slaDeadline;

    /** Penalty charged per hour of SLA breach. Range: $500 – $2,000. */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal penaltyPerHour;
}
