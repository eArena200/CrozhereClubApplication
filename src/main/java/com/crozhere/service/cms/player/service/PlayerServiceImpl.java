package com.crozhere.service.cms.player.service;

import com.crozhere.service.cms.auth.repository.entity.User;
import com.crozhere.service.cms.player.controller.model.request.UpdatePlayerRequest;
import com.crozhere.service.cms.player.repository.dao.PlayerDao;
import com.crozhere.service.cms.player.repository.dao.exception.PlayerDAOException;
import com.crozhere.service.cms.player.repository.entity.Player;
import com.crozhere.service.cms.player.service.exception.PlayerServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.swing.text.html.Option;
import java.util.Optional;

@Slf4j
@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerDao playerDao;

    @Autowired
    public PlayerServiceImpl(
            @Qualifier("PlayerSqlDao") PlayerDao playerDao){
        this.playerDao = playerDao;
    }

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
            throw new PlayerServiceException("CreatePlayerForUserException");
        }
    }

    @Override
    public Player getPlayerByUserId(Long userId) throws PlayerServiceException {
        try {
            return playerDao.findByUserId(userId).orElseThrow(PlayerDAOException::new);
        } catch (PlayerDAOException e){
            log.error("Exception while getting player for userId: {}", userId);
            throw new PlayerServiceException("GetPlayerByUserIdException");
        }
    }

    @Override
    public Player getPlayerById(Long playerId) throws PlayerServiceException {
        try {
            return playerDao.getById(playerId);
        } catch (PlayerDAOException e){
            log.error("Exception while getting Player with playerId: {}", playerId);
            throw new PlayerServiceException("GetPlayerByIdException");
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

        } catch (PlayerDAOException e){
            log.error("Exception while updating player details with playerId: {}", playerId);
            throw new PlayerServiceException("UpdatePlayerDetailsException");
        }
    }

    @Override
    public void deletePlayer(Long playerId) throws PlayerServiceException {
        try {
            playerDao.deleteById(playerId);
        } catch (PlayerDAOException e) {
            log.error("Exception while deleting player with playerId: {}", playerId);
            throw new PlayerServiceException("DeletePlayerException");
        }
    }

}
