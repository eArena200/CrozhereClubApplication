package com.crozhere.service.cms.user.controller;

import com.crozhere.service.cms.user.controller.model.request.CreatePlayerRequest;
import com.crozhere.service.cms.user.controller.model.request.UpdatePlayerRequest;
import com.crozhere.service.cms.user.controller.model.response.PlayerResponse;
import com.crozhere.service.cms.user.repository.model.Player;
import com.crozhere.service.cms.user.service.PlayerService;
import com.crozhere.service.cms.user.service.exception.PlayerServiceException;
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

    @PostMapping
    public ResponseEntity<PlayerResponse> createPlayer(
            @RequestBody CreatePlayerRequest createPlayerRequest) {
        try {
            Player player = playerService.createPlayer(createPlayerRequest);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(getPlayerResponse(player));

        } catch (PlayerServiceException e) {
            log.error("Exception in CreatePlayer for request: {}", createPlayerRequest.toString());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<PlayerResponse> getPlayerById(
            @PathVariable("playerId") String playerId) {
        try {
            Player player = playerService.getPlayer(playerId);
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
    public ResponseEntity<PlayerResponse> updatePlayer(
            @PathVariable("playerId") String playerId,
            @RequestBody UpdatePlayerRequest updatePlayerRequest) {
        try {
            Player player = playerService.updatePlayer(playerId, updatePlayerRequest);
            return ResponseEntity.
                    status(HttpStatus.OK)
                    .body(getPlayerResponse(player));

        } catch (PlayerServiceException e) {
            log.error("Exception in UpdatePlayer request for playerId: {}", playerId);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }


    @DeleteMapping("/{playerId}")
    public ResponseEntity<Void> deletePlayer(
            @PathVariable("playerId") String playerId) {
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
