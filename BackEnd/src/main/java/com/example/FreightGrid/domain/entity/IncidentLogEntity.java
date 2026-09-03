package com.example.FreightGrid.domain.entity;

import com.example.FreightGrid.domain.enums.IncidentCategory;
import com.example.FreightGrid.domain.enums.IncidentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "incident_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The shipment this incident is associated with.
     * LAZY – no CascadeType.REMOVE. Incident logs are preserved independently
     * for audit history even after shipment lifecycle ends.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private ShipmentEntity shipment;

    /** Human-readable description of the disruption (e.g., "Route blocked", "Driver HOS exceeded"). */
    @Column(nullable = false)
    private String disruptionType;

    /**
     * JSON array of mitigation strategies proposed by the AI engine (Groq/LLaMA).
     * Stored as TEXT in PostgreSQL for Sprint 1; can be cast to JSONB in a future migration.
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String aiProposedStrategies;

    /** The strategy selected by the authorised user (ROLE_ANALYST or ROLE_DIRECTOR). */
    @Column(nullable = true)
    private String selectedStrategy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus status;

    /** Root-cause category assigned by the PostMortemAgent. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private IncidentCategory category;

    /** True if the PostMortemAgent flagged this incident for urgent HR/ops review. */
    @Column(nullable = false)
    @Builder.Default
    private boolean flaggedForReview = false;

    /** Audit timestamp set automatically on insert. */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
