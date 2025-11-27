package com.drinking.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reaction_tests")
@Getter
@Setter
@NoArgsConstructor
public class ReactionTest {

    @Id
    private Long id; // user id와 동일

    @Column(name = "reaction_time", columnDefinition = "TEXT")
    private String reactionTime; // JSON 배열로 저장 예: "[250, 300, 280]"

    @Column(name = "tested_at", columnDefinition = "TEXT")
    private String testedAt; // JSON 배열로 저장 예: "['2025-01-01T10:00:00', ...]"
}