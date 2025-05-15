package com.crozhere.service.cms.player.repository.dao.impl;

import com.crozhere.service.cms.player.repository.dao.PlayerDao;
import com.crozhere.service.cms.player.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.player.repository.dao.exception.PlayerDAOException;
import com.crozhere.service.cms.player.repository.entity.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component("PlayerInMemDao")
public class PlayerInMemDao implements PlayerDao {

    private final Map<Long, Player> playerStore;
    private final Map<Long, Player> userIdIndex;

    public PlayerInMemDao(){
        this.playerStore = new HashMap<>();
        this.userIdIndex = new HashMap<>();
    }

    @Override
    public void save(Player player) throws PlayerDAOException {
        if (player.getId() == null) {
            throw new PlayerDAOException("Player ID cannot be null");
        }
        playerStore.put(player.getId(), player);
        userIdIndex.put(player.getUser().getId(), player);
    }

    @Override
    public void update(Long playerId, Player updatedPlayer)
            throws DataNotFoundException {
        if (!playerStore.containsKey(playerId)) {
            throw new DataNotFoundException("Player not found with ID: " + playerId);
        }
        updatedPlayer.setId(playerId);
        playerStore.put(playerId, updatedPlayer);
        userIdIndex.put(updatedPlayer.getUser().getId(), updatedPlayer);
    }

    @Override
    public Player getById(Long playerId) throws DataNotFoundException {
        Player player = playerStore.get(playerId);
        if (player == null) {
            throw new DataNotFoundException("Player not found with ID: " + playerId);
        }
        return player;
    }

    @Override
    public Optional<Player> findById(Long playerId) {
        return Optional.ofNullable(playerStore.get(playerId));
    }

    @Override
    public Optional<Player> findByUserId(Long userId) {
        return Optional.ofNullable(userIdIndex.get(userId));
    }

    @Override
    public void deleteById(Long id) {
        Player player = playerStore.remove(id);
        if (player != null) {
            userIdIndex.remove(player.getUser().getId());
        }
    }
}
