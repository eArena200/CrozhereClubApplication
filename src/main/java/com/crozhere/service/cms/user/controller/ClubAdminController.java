package com.crozhere.service.cms.user.controller;

import com.crozhere.service.cms.common.security.AuthUtil;
import com.crozhere.service.cms.user.controller.model.request.UpdateClubAdminRequest;
import com.crozhere.service.cms.user.controller.model.response.ClubAdminResponse;
import com.crozhere.service.cms.user.repository.entity.ClubAdmin;
import com.crozhere.service.cms.user.service.ClubAdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping(
        value = "/user/club-admin",
        produces = "application/json"
)
@Tag(name = "ClubAdmin APIs", description = "APIs for managing club admins and their details")
public class ClubAdminController {

    private final ClubAdminService clubAdminService;

    @Autowired
    public ClubAdminController(ClubAdminService clubAdminService){
        this.clubAdminService = clubAdminService;
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Get club admin by ID",
        description = "Retrieves a specific club admin's profile by their ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved club admin profile",
            content = @Content(schema = @Schema(implementation = ClubAdminResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Club admin not found"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @GetMapping("/getDetails")
    public ResponseEntity<ClubAdminResponse> getClubAdminById() {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        ClubAdmin clubAdmin = clubAdminService.getClubAdminById(clubAdminId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getClubAdminResponse(clubAdmin));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Update club admin details",
        description = "Updates the profile information for an existing club admin"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Club admin profile updated successfully",
            content = @Content(schema = @Schema(implementation = ClubAdminResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Club admin not found"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @PutMapping(
            value = "/updateDetails",
            consumes = "application/json"
    )
    public ResponseEntity<ClubAdminResponse> updateClubAdminDetails(
            @Parameter(description = "Updated club admin details", required = true)
            @RequestBody UpdateClubAdminRequest updateClubAdminRequest
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        ClubAdmin clubAdmin =
                clubAdminService.updateClubAdminDetails(clubAdminId, updateClubAdminRequest);
        return ResponseEntity.
                status(HttpStatus.OK)
                .body(getClubAdminResponse(clubAdmin));
    }

    private ClubAdminResponse getClubAdminResponse(ClubAdmin clubAdmin){
        return ClubAdminResponse.builder()
                .id(clubAdmin.getId())
                .name(clubAdmin.getName())
                .email(clubAdmin.getEmail())
                .phone(clubAdmin.getPhone())
                .build();
    }
}
