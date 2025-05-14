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
    private final Map<String, Player> phoneIndex;
    private final Map<String, Player> usernameIndex;
    private final Map<Long, Player> userIdIndex;

    public PlayerInMemDao(){
        this.playerStore = new HashMap<>();
        this.phoneIndex = new HashMap<>();
        this.usernameIndex = new HashMap<>();
        this.userIdIndex = new HashMap<>();
    }

    @Override
    public void save(Player player) throws PlayerDAOException {
        if (player.getId() == null) {
            throw new PlayerDAOException("Player ID cannot be null");
        }
        playerStore.put(player.getId(), player);
        phoneIndex.put(player.getPhone(), player);
        usernameIndex.put(player.getUsername(), player);
        userIdIndex.put(player.getUser().getId(), player);
    }

    @Override
    public void update(Long playerId, Player updatedPlayer) throws DataNotFoundException {
        if (!playerStore.containsKey(playerId)) {
            throw new DataNotFoundException("Player not found with ID: " + playerId);
        }
        updatedPlayer.setId(playerId);
        playerStore.put(playerId, updatedPlayer);
        phoneIndex.put(updatedPlayer.getPhone(), updatedPlayer);
        usernameIndex.put(updatedPlayer.getUsername(), updatedPlayer);
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
    public Player getByPhone(String phone) throws DataNotFoundException {
        Player player = phoneIndex.get(phone);
        if (player == null) {
            throw new DataNotFoundException("Player not found with phone: " + phone);
        }
        return player;
    }

    @Override
    public Optional<Player> findByPhone(String phone) {
        return Optional.ofNullable(phoneIndex.get(phone));
    }

    @Override
    public Player getByUserId(Long userId) throws DataNotFoundException {
        Player player = userIdIndex.get(userId);
        if (player == null) {
            throw new DataNotFoundException("Player not found with userId: " + userId);
        }
        return player;
    }

    @Override
    public Optional<Player> findByUserId(Long userId) {
        return Optional.ofNullable(userIdIndex.get(userId));
    }

    @Override
    public boolean existsByUsername(String username) {
        return usernameIndex.containsKey(username);
    }

    @Override
    public void deleteById(Long id) {
        Player player = playerStore.remove(id);
        if (player != null) {
            phoneIndex.remove(player.getPhone());
            usernameIndex.remove(player.getUsername());
            userIdIndex.remove(player.getUser().getId());
        }
    }
}
