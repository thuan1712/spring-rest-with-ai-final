package vn.hoidanit.springrestwithai.feature.permission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePermissionRequest(
        @NotNull(message = "ID is required")
        Long id,

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "API Path is required")
        String apiPath,

        @NotBlank(message = "Method is required")
        String method,

        @NotBlank(message = "Module is required")
        String module
) {}
