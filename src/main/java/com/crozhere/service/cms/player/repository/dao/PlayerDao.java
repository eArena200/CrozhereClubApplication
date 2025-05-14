package com.crozhere.service.cms.player.repository.dao;

import com.crozhere.service.cms.player.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.player.repository.dao.exception.PlayerDAOException;
import com.crozhere.service.cms.player.repository.entity.Player;

import java.util.Optional;

public interface PlayerDao {

    void save(Player player) throws PlayerDAOException;

    void update(Long playerId, Player updatedPlayer) throws DataNotFoundException, PlayerDAOException;

    Player getById(Long playerId) throws DataNotFoundException, PlayerDAOException;

    Optional<Player> findById(Long playerId) throws PlayerDAOException;

    Player getByPhone(String phone) throws DataNotFoundException, PlayerDAOException;

    Optional<Player> findByPhone(String phone) throws PlayerDAOException;

    Player getByUserId(Long userId) throws DataNotFoundException, PlayerDAOException;

    Optional<Player> findByUserId(Long userId) throws PlayerDAOException;

    boolean existsByUsername(String username) throws PlayerDAOException;

    void deleteById(Long id) throws PlayerDAOException;
}
