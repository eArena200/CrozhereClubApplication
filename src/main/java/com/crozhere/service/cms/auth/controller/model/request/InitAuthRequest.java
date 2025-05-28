package com.crozhere.service.cms.auth.controller.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.Pattern;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitAuthRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[1-9]\\d{9}$",
            message = "Phone number should be valid"
    )
    private String phone;
}
