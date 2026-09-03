package com.example.FreightGrid.domain.entity;

import com.example.FreightGrid.domain.enums.VehicleStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique vehicle identifier (e.g., licence plate or internal code).
     * Enforced at DB level via unique index.
     */
    @Column(nullable = false, unique = true)
    private String vehicleCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    /**
     * The logistics node where the vehicle is currently located.
     * LAZY to avoid N+1 queries when loading large fleets.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_node_id", nullable = true)
    private NodeEntity currentNode;

    /**
     * Driver Hours-of-Service remaining (FMCSA property-carrying: max 11.0 h).
     */
    @Column(nullable = false)
    private Double driverHosRemaining;

    /** Operational cost per mile. Stored with scale 2. */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costPerMile;
}
