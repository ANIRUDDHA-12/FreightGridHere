package com.example.FreightGrid.domain.entity;

import com.example.FreightGrid.domain.enums.NodeType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nodes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NodeType type;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    /** Maximum cargo capacity in units (e.g., pallets or tonnes) */
    @Column(nullable = false)
    private Integer capacity;
}
