package vn.hoidanit.springrestwithai.feature.company.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCompanyRequest(
        @NotNull(message = "Id is required")
        Long id,

        @NotBlank(message = "Name is required")
        String name,

        String description,

        String address,

        String logo
) {
}
