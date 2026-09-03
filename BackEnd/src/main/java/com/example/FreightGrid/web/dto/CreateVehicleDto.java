package com.example.FreightGrid.web.dto;

import com.example.FreightGrid.domain.enums.VehicleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateVehicleDto(
        @NotBlank
        String vehicleId,

        @NotNull
        BigDecimal costPerMile,

        @NotNull
        Double driverHosRemaining,

        @NotNull()
        VehicleStatus status
) {
}
