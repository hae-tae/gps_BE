package com.drinking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class DrinkDtos {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddDrinkRequest {
        private Long userId;
        private String drinkType;
        private Integer count;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DrinkRecordResponse {
        private Integer sojuCount;
        private Integer beerCount;
        private Integer somaekCount;
        private Integer makgeolliCount;
        private Integer fruitCount;
        private Double totalSoju;
    }
}