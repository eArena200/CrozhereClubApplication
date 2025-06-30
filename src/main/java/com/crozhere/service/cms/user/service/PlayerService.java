package com.crozhere.service.cms.user.service;

import com.crozhere.service.cms.user.repository.entity.User;
import com.crozhere.service.cms.user.controller.model.request.UpdatePlayerRequest;
import com.crozhere.service.cms.user.repository.entity.Player;
import com.crozhere.service.cms.user.service.exception.PlayerServiceException;

public interface PlayerService {
    Player createPlayerForUser(User user) throws PlayerServiceException;
    Player getPlayerByUserId(Long userId) throws PlayerServiceException;
    Player getPlayerById(Long playerId) throws PlayerServiceException;
    Player updatePlayerDetails(Long playerId, UpdatePlayerRequest updatePlayerRequest)
            throws PlayerServiceException;
    void deletePlayer(Long playerId) throws PlayerServiceException;
}
