package com.crozhere.service.cms.player.repository.entity;

import com.crozhere.service.cms.auth.repository.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "player")
public class Player {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign key relationship with player
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "name")
    private String name;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "email")
    private String email;
}