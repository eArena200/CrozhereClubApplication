package com.crozhere.service.cms.user.repository;

import com.crozhere.service.cms.user.repository.exception.PlayerDAOException;
import com.crozhere.service.cms.user.repository.model.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("PlayerInMemDAO")
public class PlayerInMemDAO implements PlayerDAO {

    private final Map<String, Player> playerStore;

    public PlayerInMemDAO(){
        this.playerStore = new HashMap<>();
    }

    @Override
    public void save(Player player) throws PlayerDAOException {
        if(playerStore.containsKey(player.getId())){
            log.info("PlayerId {} already exists", player.getId());
            throw new PlayerDAOException("SaveException");
        }

        playerStore.putIfAbsent(player.getId(), player);
    }

    @Override
    public Player get(String playerId) throws PlayerDAOException {
        if(playerStore.containsKey(playerId)){
            return playerStore.get(playerId);
        } else {
            log.info("PlayerId {} doesn't exist", playerId);
            throw new PlayerDAOException("ReadException");
        }
    }

    @Override
    public void update(String playerId, Player player) throws PlayerDAOException {
        if(playerStore.containsKey(playerId)){
            playerStore.put(playerId, player);
        } else {
            log.info("PlayerId {} doesn't exist for update", playerId);
            throw new PlayerDAOException("UpdateException");
        }
    }

    @Override
    public void delete(String playerId) throws PlayerDAOException {
        if(playerStore.containsKey(playerId)){
            playerStore.remove(playerId);
        } else {
            log.info("PlayerId {} doesn't exist for delete", playerId);
            throw new PlayerDAOException("DeleteException");
        }
    }

    @Override
    public Player getPlayerByPhone(String phone) throws PlayerDAOException {
        List<Player> players = playerStore.values().stream()
                .filter(player -> player.getPhone().equals(phone))
                .toList();

        if(players.isEmpty()){
            log.info("Player with phone {} doesn't exist", phone);
            throw new PlayerDAOException("GetPlayerByPhoneException");
        }

        return players.get(0);
    }
}
