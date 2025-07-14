package com.crozhere.service.cms.user.repository.dao.impl;

import com.crozhere.service.cms.user.repository.PlayerRepository;
import com.crozhere.service.cms.user.repository.dao.PlayerDao;
import com.crozhere.service.cms.user.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.user.repository.dao.exception.PlayerDAOException;
import com.crozhere.service.cms.user.repository.entity.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PlayerDaoImpl implements PlayerDao {

    private final PlayerRepository playerRepository;

    @Override
    public void save(Player player) throws PlayerDAOException {
        try {
            playerRepository.save(player);
        } catch (Exception e) {
            throw new PlayerDAOException("Failed to save player", e);
        }
    }

    @Override
    public void update(Long playerId, Player updatedPlayer)
            throws DataNotFoundException, PlayerDAOException {
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
    public List<Player> getPlayersByIds(List<Long> playerIds) throws PlayerDAOException {
        try{
            if(playerIds == null || playerIds.isEmpty()){
                return List.of();
            }
            return playerRepository.findAllById(playerIds);
        } catch (Exception e){
            throw new PlayerDAOException("Failed to fetch players for playerIds");
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

    @Override
    public Optional<Player> findByUserId(Long userId) {
        return playerRepository.findByUser_Id(userId);
    }

}
