package com.crozhere.service.cms.auth.controller;

import com.crozhere.service.cms.auth.controller.model.request.InitAuthRequest;
import com.crozhere.service.cms.auth.controller.model.request.VerifyAuthRequest;
import com.crozhere.service.cms.auth.controller.model.response.VerifyAuthResponse;
import com.crozhere.service.cms.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "APIs for handling user authentication and verification")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
        summary = "Initialize authentication",
        description = "Initiates the authentication process by sending a verification code to the user's phone number"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Authentication initialization successful"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error or SMS service failure"
        )
    })
    @PostMapping("/init")
    public ResponseEntity<Void> initAuth(
            @Parameter(description = "Authentication initialization request containing phone number", required = true)
            @Valid @RequestBody InitAuthRequest initAuthRequest) {
        authService.initAuth(initAuthRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Verify authentication",
        description = "Verifies the authentication code sent to the user's phone number and returns authentication token"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Authentication successful",
            content = @Content(schema = @Schema(implementation = VerifyAuthResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters or verification code"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Authentication session not found"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @PostMapping("/verify")
    public ResponseEntity<VerifyAuthResponse> verifyAuth(
            @Parameter(description = "Authentication verification request containing phone number and verification code", required = true)
            @Valid @RequestBody VerifyAuthRequest verifyAuthRequest) {
        VerifyAuthResponse response = authService.verifyAuth(verifyAuthRequest);
        return ResponseEntity.ok(response);
    }
}
