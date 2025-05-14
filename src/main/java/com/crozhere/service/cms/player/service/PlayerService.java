package com.crozhere.service.cms.player.service;

import com.crozhere.service.cms.auth.repository.entity.User;
import com.crozhere.service.cms.player.controller.model.request.UpdatePlayerRequest;
import com.crozhere.service.cms.player.repository.entity.Player;
import com.crozhere.service.cms.player.service.exception.PlayerServiceException;

public interface PlayerService {
    Player createPlayerForUser(User user) throws PlayerServiceException;
    Player getPlayerByUserId(Long userId) throws PlayerServiceException;
    Player getPlayerById(Long playerId) throws PlayerServiceException;
    Player updatePlayerDetails(Long playerId, UpdatePlayerRequest updatePlayerRequest)
            throws PlayerServiceException;
    void deletePlayer(Long playerId) throws PlayerServiceException;
}
