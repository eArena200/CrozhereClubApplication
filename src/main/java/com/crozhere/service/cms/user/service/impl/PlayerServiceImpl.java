package com.crozhere.service.cms.user.service.impl;

import com.crozhere.service.cms.user.repository.entity.User;
import com.crozhere.service.cms.user.controller.model.request.UpdatePlayerRequest;
import com.crozhere.service.cms.user.repository.dao.PlayerDao;
import com.crozhere.service.cms.user.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.user.repository.dao.exception.PlayerDAOException;
import com.crozhere.service.cms.user.repository.entity.Player;
import com.crozhere.service.cms.user.service.exception.PlayerServiceException;
import com.crozhere.service.cms.user.service.exception.PlayerServiceExceptionType;
import com.crozhere.service.cms.user.service.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerDao playerDao;

    @Override
    public Player createPlayerForUser(User user) throws PlayerServiceException {
        try {
            Player player = Player.builder()
                    .user(user)
                    .username("Player@" + user.getPhone())
                    .name("Player@" + user.getPhone())
                    .phone(user.getPhone())
                    .email("player@crozhere.com")
                    .build();
            playerDao.save(player);
            return player;
        } catch (PlayerDAOException e){
            log.error("Exception while saving newly created for userId: {}", user.getId());
            throw new PlayerServiceException(
                    PlayerServiceExceptionType.CREATE_PLAYER_FAILED);
        }
    }

    @Override
    public Player updatePlayerDetails(Long playerId, UpdatePlayerRequest updatePlayerRequest)
            throws PlayerServiceException {
        try {
            Player player = playerDao.getById(playerId);

            if(StringUtils.hasText(updatePlayerRequest.getEmail())){
                player.setEmail(updatePlayerRequest.getEmail());
            }

            if(StringUtils.hasText(updatePlayerRequest.getUsername())){
                player.setUsername(updatePlayerRequest.getUsername());
            }

            if(StringUtils.hasText(updatePlayerRequest.getName())){
                player.setName(updatePlayerRequest.getName());
            }

            playerDao.update(playerId, player);
            return player;

        } catch (DataNotFoundException e){
            log.error("No player found with playerId {} for update", playerId);
            throw new PlayerServiceException(PlayerServiceExceptionType.PLAYER_NOT_FOUND);
        } catch (PlayerDAOException e){
            log.error("Exception while updating player details with playerId: {}", playerId);
            throw new PlayerServiceException(PlayerServiceExceptionType.UPDATE_PLAYER_FAILED);
        }
    }

    @Override
    public void deletePlayer(Long playerId) throws PlayerServiceException {
        try {
            playerDao.deleteById(playerId);
        } catch (PlayerDAOException e) {
            log.error("Exception while deleting player with playerId: {}", playerId);
            throw new PlayerServiceException(PlayerServiceExceptionType.DELETE_PLAYER_FAILED);
        }
    }


    @Override
    public Player getPlayerByUserId(Long userId) throws PlayerServiceException {
        try {
            return playerDao.findByUserId(userId).orElseThrow(DataNotFoundException::new);
        } catch (DataNotFoundException e){
            log.error("No player found for userId: {}", userId);
            throw new PlayerServiceException(PlayerServiceExceptionType.PLAYER_NOT_FOUND);
        } catch (PlayerDAOException e){
            log.error("Exception while getting player for userId: {}", userId);
            throw new PlayerServiceException(PlayerServiceExceptionType.GET_PLAYER_FAILED);
        }
    }

    @Override
    public Player getPlayerById(Long playerId) throws PlayerServiceException {
        try {
            return playerDao.getById(playerId);
        } catch (DataNotFoundException e){
            log.error("No player found for playerId: {}", playerId);
            throw new PlayerServiceException(PlayerServiceExceptionType.PLAYER_NOT_FOUND);
        } catch (PlayerDAOException e){
            log.error("Exception while getting Player with playerId: {}", playerId);
            throw new PlayerServiceException(PlayerServiceExceptionType.GET_PLAYER_FAILED);
        }
    }

    @Override
    public List<Player> getPlayersByIds(List<Long> playerIds) throws PlayerServiceException {
        try {
            return playerDao.getPlayersByIds(playerIds);
        } catch (PlayerDAOException e){
            log.error("Exception while getting players for playerIds: {}", playerIds, e);
            throw new PlayerServiceException(PlayerServiceExceptionType.GET_PLAYERS_FAILED);
        }
    }
}
