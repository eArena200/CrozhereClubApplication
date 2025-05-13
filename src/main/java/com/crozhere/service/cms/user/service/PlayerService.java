package com.crozhere.service.cms.user.service;

import com.crozhere.service.cms.user.controller.model.request.CreatePlayerRequest;
import com.crozhere.service.cms.user.controller.model.request.UpdatePlayerRequest;
import com.crozhere.service.cms.user.repository.model.Player;
import com.crozhere.service.cms.user.service.exception.PlayerServiceException;

public interface PlayerService {
    Player createPlayer(CreatePlayerRequest createPlayerRequest)
            throws PlayerServiceException;
    Player getPlayer(String playerId) throws PlayerServiceException;
    Player updatePlayer(String playerId, UpdatePlayerRequest updatePlayerRequest)
            throws PlayerServiceException;
    void deletePlayer(String playerId) throws PlayerServiceException;

    Player getOrCreatePlayerByPhone(String phone) throws PlayerServiceException;
}
