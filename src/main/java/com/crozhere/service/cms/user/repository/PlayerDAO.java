package com.crozhere.service.cms.user.repository;

import com.crozhere.service.cms.user.repository.exception.PlayerDAOException;
import com.crozhere.service.cms.user.repository.model.Player;

public interface PlayerDAO {
    void save(Player player) throws PlayerDAOException;
    Player get(String playerId) throws PlayerDAOException;
    void update(String playerId, Player player) throws PlayerDAOException;
    void delete(String playerId) throws PlayerDAOException;
}
