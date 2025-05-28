package com.crozhere.service.cms.auth.controller.model.request;

import com.crozhere.service.cms.auth.repository.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyAuthRequest {
    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[1-9]\\d{9}$",
            message = "Phone number should be valid"
    )
    private String phone;

    @NotBlank(message = "OTP is required")
    private String otp;


    @NotNull(message = "Role is required")
    private UserRole role;
}
