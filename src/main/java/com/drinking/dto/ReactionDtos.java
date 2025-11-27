package com.drinking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

public class ReactionDtos {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveReactionRequest {
        private Long userId;
        private Integer reactionTime;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReactionTestResponse {
        private List<Integer> reactionTimes;
        private List<String> testedAt;
        private Double averageTime;
    }
}