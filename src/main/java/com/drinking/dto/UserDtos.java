package com.drinking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UserDtos {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResultResponse {
        private String userName;
        private String characterLevel;
        private Double totalSoju;
        private Double sojuPerHour;
        private Integer ranking;
        private Double averageReactionTime;
        private String aiComment;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RankingResponse {
        private Long userId;
        private String userName;
        private Double totalSoju;
        private Integer ranking;
    }
}