package com.crozhere.service.cms.user.service;

import com.crozhere.service.cms.user.controller.model.request.CreatePlayerRequest;
import com.crozhere.service.cms.user.controller.model.request.UpdatePlayerRequest;
import com.crozhere.service.cms.user.repository.PlayerDAO;
import com.crozhere.service.cms.user.repository.exception.PlayerDAOException;
import com.crozhere.service.cms.user.repository.model.Player;
import com.crozhere.service.cms.user.service.exception.PlayerServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Slf4j
@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerDAO playerDao;

    @Autowired
    public PlayerServiceImpl(
            @Qualifier("PlayerInMemDAO") PlayerDAO playerDao){
        this.playerDao = playerDao;
    }

    @Override
    public Player createPlayer(CreatePlayerRequest createPlayerRequest)
            throws PlayerServiceException {
        Player player = Player.builder()
                .id(UUID.randomUUID().toString())
                .email(createPlayerRequest.getEmail())
                .phone(createPlayerRequest.getPhone())
                .name(createPlayerRequest.getName())
                .username(createPlayerRequest.getUsername())
                .build();
        try {
            playerDao.save(player);
            return player;
        } catch (PlayerDAOException e){
            log.error("Exception while saving playerId: {}", player.getId());
            throw new PlayerServiceException("CreatePlayerException", e);
        }
    }

    @Override
    public Player getPlayer(String playerId) throws PlayerServiceException {
        try {
            return playerDao.get(playerId);
        } catch (PlayerDAOException e) {
            log.error("Exception while retrieving playerId: {}", playerId);
            throw new PlayerServiceException("GetPlayerException", e);
        }
    }

    @Override
    public Player updatePlayer(String playerId, UpdatePlayerRequest updatePlayerRequest)
            throws PlayerServiceException {
        try {
            Player player = playerDao.get(playerId);
            if(StringUtils.hasText(updatePlayerRequest.getName())){
                player.setName(updatePlayerRequest.getName());
            }
            if(StringUtils.hasText(updatePlayerRequest.getEmail())){
                player.setEmail(updatePlayerRequest.getEmail());
            }
            if(StringUtils.hasText(updatePlayerRequest.getUsername())){
                player.setUsername(updatePlayerRequest.getUsername());
            }

            playerDao.update(playerId, player);
            return player;
        } catch (PlayerDAOException e) {
            log.error("Exception while updating playerId: {}", playerId);
            throw new PlayerServiceException("UpdatePlayerException", e);
        }
    }

    @Override
    public void deletePlayer(String playerId) throws PlayerServiceException {
        try {
            playerDao.delete(playerId);
        } catch (PlayerDAOException e) {
            log.error("Exception while deleting playerId: {}", playerId);
            throw new PlayerServiceException("DeletePlayerException", e);
        }
    }

    @Override
    public Player getOrCreatePlayerByPhone(String phone) throws PlayerServiceException {
        try {
            return playerDao.getPlayerByPhone(phone);
        } catch (PlayerDAOException e){
            log.info("Player not found with phone number {}", phone);
            return createPlayer(
                    CreatePlayerRequest.builder()
                    .phone(phone)
                    .build());
        }
    }
}
