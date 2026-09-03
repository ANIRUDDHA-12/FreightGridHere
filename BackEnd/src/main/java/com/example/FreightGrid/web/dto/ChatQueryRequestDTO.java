package com.example.FreightGrid.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Inbound request body for the chatbot endpoint.
 *
 * @param query the user's natural-language query
 */
public record ChatQueryRequestDTO(@NotBlank String query) {}
