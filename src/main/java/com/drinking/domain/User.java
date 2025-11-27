package com.drinking.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_code", nullable = false)
    private String roomCode;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "character_level")
    private String characterLevel;

    @Column(name = "ranking")
    private Integer ranking;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "total_soju")
    private Double totalSoju;

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }
}