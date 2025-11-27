package com.drinking.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DrinkType {
    SOJU("소주", 1.0),       // 기준
    BEER("맥주", 0.3),       // 500cc 맥주는 소주 1/3병 정도 알코올 양이라 가정 (단순화)
    SOMAEK("소맥", 1.2),     // 소주 + 맥주
    FRUIT("과일소주", 0.8);  // 도수가 낮음

    private final String description;
    private final double sojuEquivalent; // 소주 1잔 대비 환산 비율
}