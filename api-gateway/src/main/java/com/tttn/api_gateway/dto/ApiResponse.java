package com.tttn.api_gateway.dto;

public record ApiResponse(
        Integer code,
        String message
) {
}
