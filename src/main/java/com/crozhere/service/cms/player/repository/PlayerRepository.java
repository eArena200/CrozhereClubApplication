package com.crozhere.service.cms.player.repository;

import com.crozhere.service.cms.auth.repository.entity.User;
import com.crozhere.service.cms.player.repository.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    Optional<Player> findByUsername(String username);

    Optional<Player> findByUser(User user);

    Optional<Player> findByUser_Id(Long userId);

    Optional<Player> findByPhone(String phone);

    boolean existsByUsername(String username);
}
