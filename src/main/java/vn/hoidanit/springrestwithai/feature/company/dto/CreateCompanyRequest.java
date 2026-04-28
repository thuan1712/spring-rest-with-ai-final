package vn.hoidanit.springrestwithai.feature.company.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCompanyRequest(
        @NotBlank(message = "Name is required")
        String name,

        String description,

        String address,

        String logo
) {
}
