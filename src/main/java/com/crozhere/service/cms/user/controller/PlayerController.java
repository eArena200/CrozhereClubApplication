package com.crozhere.service.cms.user.controller;

import com.crozhere.service.cms.common.security.AuthUtil;
import com.crozhere.service.cms.user.controller.model.request.UpdatePlayerRequest;
import com.crozhere.service.cms.user.controller.model.response.PlayerResponse;
import com.crozhere.service.cms.user.repository.entity.Player;
import com.crozhere.service.cms.user.service.PlayerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
        value = "/user/player",
        produces = "application/json"
)
@Tag(name = "Player APIs", description = "APIs for managing player profiles and details")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(
            PlayerService playerService
    ){
        this.playerService = playerService;
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Get player by ID",
        description = "Retrieves a specific player's profile by their ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved player profile",
            content = @Content(schema = @Schema(implementation = PlayerResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Player not found"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @PreAuthorize("hasRole('PLAYER')")
    @GetMapping("/getDetails")
    public ResponseEntity<PlayerResponse> getPlayerById() {
        Long playerId = AuthUtil.getRoleBasedId();
        Player player = playerService.getPlayerById(playerId);
        return ResponseEntity.ok(getPlayerResponse(player));
    }


    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Update player details",
        description = "Updates the profile information for an existing player"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Player profile updated successfully",
            content = @Content(schema = @Schema(implementation = PlayerResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Player not found"
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
    @PreAuthorize("hasRole('PLAYER')")
    @PutMapping(
            value = "/updateDetails",
            consumes = "application/json"
    )
    public ResponseEntity<PlayerResponse> updatePlayerDetails(
            @Parameter(description = "Updated player details", required = true)
            @RequestBody UpdatePlayerRequest updatePlayerRequest
    ) {
        Long playerId = AuthUtil.getRoleBasedId();
        Player player = playerService.updatePlayerDetails(playerId, updatePlayerRequest);
        return ResponseEntity.ok(getPlayerResponse(player));
    }

    private PlayerResponse getPlayerResponse(Player player){
        return PlayerResponse.builder()
                .id(player.getId())
                .username(player.getUsername())
                .email(player.getEmail())
                .phone(player.getPhone())
                .name(player.getName())
                .build();
    }
}
