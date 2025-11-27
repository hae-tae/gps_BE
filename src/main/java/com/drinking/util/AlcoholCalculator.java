package com.drinking.util;


import com.drinking.domain.enums.DrinkType;
import java.util.Map;

public class AlcoholCalculator {

    // 총 마신 술을 '소주 잔' 단위로 환산
    public static double calculateSojuEquivalent(Map<DrinkType, Integer> drinkCounts) {
        return drinkCounts.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getSojuEquivalent() * entry.getValue())
                .sum();
    }

    // 시속 몇 잔인지 계산
    public static double calculateDrinksPerHour(double totalSoju, long secondsElapsed) {
        if (secondsElapsed == 0) return 0;
        double hours = secondsElapsed / 3600.0;
        return totalSoju / hours;
    }
}