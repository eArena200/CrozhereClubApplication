package com.crozhere.service.cms.player.controller;

import com.crozhere.service.cms.player.controller.model.request.UpdatePlayerRequest;
import com.crozhere.service.cms.player.controller.model.response.PlayerResponse;
import com.crozhere.service.cms.player.repository.entity.Player;
import com.crozhere.service.cms.player.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
        Player player = playerService.getPlayerById(playerId);
        return ResponseEntity.ok(getPlayerResponse(player));
    }


    @PutMapping("/{playerId}")
    public ResponseEntity<PlayerResponse> updatePlayerDetails(
            @PathVariable("playerId") Long playerId,
            @RequestBody UpdatePlayerRequest updatePlayerRequest) {
        Player player = playerService.updatePlayerDetails(playerId, updatePlayerRequest);
        return ResponseEntity.ok(getPlayerResponse(player));
    }


    @DeleteMapping("/{playerId}")
    public ResponseEntity<Void> deletePlayer(
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
