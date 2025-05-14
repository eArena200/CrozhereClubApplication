package com.crozhere.service.cms.player.repository.dao.impl;

import com.crozhere.service.cms.player.repository.PlayerRepository;
import com.crozhere.service.cms.player.repository.dao.PlayerDao;
import com.crozhere.service.cms.player.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.player.repository.dao.exception.PlayerDAOException;
import com.crozhere.service.cms.player.repository.entity.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component("PlayerSqlDao")
public class PlayerSqlDao implements PlayerDao {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerSqlDao(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public void save(Player player) throws PlayerDAOException {
        try {
            playerRepository.save(player);
        } catch (Exception e) {
            throw new PlayerDAOException("Failed to save player", e);
        }
    }

    @Override
    public void update(Long playerId, Player updatedPlayer) throws DataNotFoundException, PlayerDAOException {
        try {
            if (!playerRepository.existsById(playerId)) {
                throw new DataNotFoundException("Player not found with ID: " + playerId);
            }
            updatedPlayer.setId(playerId);
            playerRepository.save(updatedPlayer);
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new PlayerDAOException("Failed to update player", e);
        }
    }

    @Override
    public Player getById(Long playerId) throws DataNotFoundException, PlayerDAOException {
        try {
            return playerRepository.findById(playerId)
                    .orElseThrow(() -> new DataNotFoundException("Player not found with ID: " + playerId));
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new PlayerDAOException("Failed to get player by ID", e);
        }
    }

    @Override
    public Optional<Player> findById(Long playerId) throws PlayerDAOException {
        try {
            return playerRepository.findById(playerId);
        } catch (Exception e) {
            throw new PlayerDAOException("Failed to find player by ID", e);
        }
    }

    @Override
    public Player getByPhone(String phone) throws DataNotFoundException, PlayerDAOException {
        try {
            return playerRepository.findByPhone(phone)
                    .orElseThrow(() -> new DataNotFoundException("Player not found with phone: " + phone));
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new PlayerDAOException("Failed to get player by phone", e);
        }
    }

    @Override
    public Optional<Player> findByPhone(String phone) throws PlayerDAOException {
        try {
            return playerRepository.findByPhone(phone);
        } catch (Exception e) {
            throw new PlayerDAOException("Failed to find player by phone", e);
        }
    }

    @Override
    public Player getByUserId(Long userId) throws DataNotFoundException {
        return playerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new DataNotFoundException("Player not found with userId: " + userId));
    }

    @Override
    public Optional<Player> findByUserId(Long userId) {
        return playerRepository.findByUser_Id(userId);
    }

    @Override
    public boolean existsByUsername(String username) throws PlayerDAOException {
        try {
            return playerRepository.existsByUsername(username);
        } catch (Exception e) {
            throw new PlayerDAOException("Failed to check username existence", e);
        }
    }

    @Override
    public void deleteById(Long id) throws PlayerDAOException {
        try {
            playerRepository.deleteById(id);
        } catch (Exception e) {
            throw new PlayerDAOException("Failed to delete player", e);
        }
    }
}
