package com.crozhere.service.cms.player.controller;

import com.crozhere.service.cms.player.controller.model.request.UpdatePlayerRequest;
import com.crozhere.service.cms.player.controller.model.response.PlayerResponse;
import com.crozhere.service.cms.player.repository.entity.Player;
import com.crozhere.service.cms.player.service.PlayerService;
import com.crozhere.service.cms.player.service.exception.PlayerServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService){
        this.playerService = playerService;
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<PlayerResponse> getPlayerById(
            @PathVariable("playerId") Long playerId) {
        try {
            Player player = playerService.getPlayerById(playerId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(getPlayerResponse(player));

        } catch (PlayerServiceException e) {
            log.error("Exception in GetPlayerById request for playerId: {}", playerId);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }


    @PutMapping("/{playerId}")
    public ResponseEntity<PlayerResponse> updatePlayerDetails(
            @PathVariable("playerId") Long playerId,
            @RequestBody UpdatePlayerRequest updatePlayerRequest) {
        try {
            Player player = playerService.updatePlayerDetails(playerId, updatePlayerRequest);
            return ResponseEntity.
                    status(HttpStatus.OK)
                    .body(getPlayerResponse(player));

        } catch (PlayerServiceException e) {
            log.error("Exception in UpdatePlayerDetails request for playerId: {}", playerId);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }


    @DeleteMapping("/{playerId}")
    public ResponseEntity<Void> deletePlayer(
            @PathVariable("playerId") Long playerId) {
        try {
            playerService.deletePlayer(playerId);
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();

        } catch (PlayerServiceException e) {
            log.error("Exception in DeletePlayer request for playerId: {}", playerId);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
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
