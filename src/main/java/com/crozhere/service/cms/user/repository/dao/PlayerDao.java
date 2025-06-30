package com.crozhere.service.cms.user.repository.dao;

import com.crozhere.service.cms.user.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.user.repository.dao.exception.PlayerDAOException;
import com.crozhere.service.cms.user.repository.entity.Player;

import java.util.Optional;

public interface PlayerDao {

    void save(Player player) throws PlayerDAOException;

    void update(Long playerId, Player updatedPlayer)
            throws DataNotFoundException, PlayerDAOException;

    Player getById(Long playerId) throws DataNotFoundException, PlayerDAOException;
    Optional<Player> findById(Long playerId) throws PlayerDAOException;

    void deleteById(Long id) throws PlayerDAOException;

    Optional<Player> findByUserId(Long userId) throws PlayerDAOException;
}
