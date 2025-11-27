package com.drinking.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
public class Room {

    @Id
    @Column(name = "room_code", length = 4)
    private String roomCode;

    @Column(name = "room_name", nullable = false)
    private String roomName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "status")
    private Integer status = 0; // 0: 시작 전, 1: 시작, 2: 끝남

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}