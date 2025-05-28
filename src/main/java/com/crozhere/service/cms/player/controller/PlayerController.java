package com.crozhere.service.cms.player.controller;

import com.crozhere.service.cms.player.controller.model.request.UpdatePlayerRequest;
import com.crozhere.service.cms.player.controller.model.response.PlayerResponse;
import com.crozhere.service.cms.player.repository.entity.Player;
import com.crozhere.service.cms.player.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/players")
@Tag(name = "Player Management", description = "APIs for managing player profiles and details")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService){
        this.playerService = playerService;
    }

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
    @GetMapping("/{playerId}")
    public ResponseEntity<PlayerResponse> getPlayerById(
            @Parameter(description = "ID of the player to retrieve", required = true)
            @PathVariable("playerId") Long playerId) {
        Player player = playerService.getPlayerById(playerId);
        return ResponseEntity.ok(getPlayerResponse(player));
    }


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
    @PutMapping("/{playerId}")
    public ResponseEntity<PlayerResponse> updatePlayerDetails(
            @Parameter(description = "ID of the player to update", required = true)
            @PathVariable("playerId") Long playerId,
            @Parameter(description = "Updated player details", required = true)
            @RequestBody UpdatePlayerRequest updatePlayerRequest) {
        Player player = playerService.updatePlayerDetails(playerId, updatePlayerRequest);
        return ResponseEntity.ok(getPlayerResponse(player));
    }


    @Operation(
        summary = "Delete player",
        description = "Deletes a player's profile and associated data"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Player deleted successfully"
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
    @DeleteMapping("/{playerId}")
    public ResponseEntity<Void> deletePlayer(
            @Parameter(description = "ID of the player to delete", required = true)
            @PathVariable("playerId") Long playerId) {
            playerService.deletePlayer(playerId);
            return ResponseEntity.noContent().build();
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
