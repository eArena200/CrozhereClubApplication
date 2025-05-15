package com.crozhere.service.cms.player.repository;

import com.crozhere.service.cms.player.repository.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByUser_Id(Long userId);
}
