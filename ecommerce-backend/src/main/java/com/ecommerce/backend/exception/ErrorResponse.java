package com.ecommerce.backend.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Error response")
public class ErrorResponse {

    @Schema(description = "Error timestamp", example = "2023-12-01T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error type", example = "Bad Request")
    private String error;

    @Schema(description = "Error message", example = "Invalid input data")
    private String message;

    @Schema(description = "Request path", example = "/api/v1/products")
    private String path;

    @Schema(description = "Validation errors for specific fields")
    private Map<String, String> validationErrors;
}