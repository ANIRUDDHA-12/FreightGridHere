package com.example.FreightGrid.domain.repository;

import com.example.FreightGrid.domain.entity.NodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeRepository extends JpaRepository<NodeEntity, Long> {
}
