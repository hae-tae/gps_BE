package com.drinking.service;

import com.drinking.domain.DrinkRecord;
import com.drinking.domain.User;
import com.drinking.dto.DrinkDtos;
import com.drinking.repository.DrinkRecordRepository;
import com.drinking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DrinkService {

    private final DrinkRecordRepository drinkRecordRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 알코올 함량 (소주 기준으로 환산)
    private static final double SOJU_STANDARD = 1.0;      // 소주 1잔 = 1
    private static final double BEER_TO_SOJU = 0.3;       // 맥주 1잔 = 소주 0.3잔
    private static final double SOMAEK_TO_SOJU = 1.3;     // 소맥 1잔 = 소주 1.3잔
    private static final double MAKGEOLLI_TO_SOJU = 0.4;  // 막걸리 1잔 = 소주 0.4잔
    private static final double FRUIT_TO_SOJU = 0.7;      // 과일소주 1잔 = 소주 0.7잔

    @Transactional
    public DrinkDtos.DrinkRecordResponse addDrink(DrinkDtos.AddDrinkRequest request) {
        DrinkRecord record = drinkRecordRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        int currentCount = 0;
        switch (request.getDrinkType().toLowerCase()) {
            case "soju":
                currentCount = record.getSojuCount() + request.getCount();
                record.setSojuCount(Math.max(0, currentCount)); // 음수 방지
                break;
            case "beer":
                currentCount = record.getBeerCount() + request.getCount();
                record.setBeerCount(Math.max(0, currentCount));
                break;
            case "somaek":
                currentCount = record.getSomaekCount() + request.getCount();
                record.setSomaekCount(Math.max(0, currentCount));
                break;
            case "makgeolli":
                currentCount = record.getMakgeolliCount() + request.getCount();
                record.setMakgeolliCount(Math.max(0, currentCount));
                break;
            case "fruit":
                currentCount = record.getFruitCount() + request.getCount();
                record.setFruitCount(Math.max(0, currentCount));
                break;
            default:
                throw new IllegalArgumentException("잘못된 술 종류입니다.");
        }

        drinkRecordRepository.save(record);

        // 총 소주 환산량 계산
        double totalSoju = calculateTotalSoju(record);
        user.setTotalSoju(totalSoju);
        userRepository.save(user);

        // WebSocket으로 업데이트 전송
        messagingTemplate.convertAndSend(
                "/topic/room/" + user.getRoomCode() + "/drinks",
                new DrinkUpdate(user.getId(), user.getUserName(), totalSoju)
        );

        return new DrinkDtos.DrinkRecordResponse(
                record.getSojuCount(),
                record.getBeerCount(),
                record.getSomaekCount(),
                record.getMakgeolliCount(),
                record.getFruitCount(),
                totalSoju
        );
    }

    @Transactional(readOnly = true)
    public DrinkDtos.DrinkRecordResponse getDrinkRecord(Long userId) {
        DrinkRecord record = drinkRecordRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("기록을 찾을 수 없습니다."));

        double totalSoju = calculateTotalSoju(record);

        return new DrinkDtos.DrinkRecordResponse(
                record.getSojuCount(),
                record.getBeerCount(),
                record.getSomaekCount(),
                record.getMakgeolliCount(),
                record.getFruitCount(),
                totalSoju
        );
    }

    private double calculateTotalSoju(DrinkRecord record) {
        return (record.getSojuCount() * SOJU_STANDARD) +
                (record.getBeerCount() * BEER_TO_SOJU) +
                (record.getSomaekCount() * SOMAEK_TO_SOJU) +
                (record.getMakgeolliCount() * MAKGEOLLI_TO_SOJU) +
                (record.getFruitCount() * FRUIT_TO_SOJU);
    }

    @lombok.AllArgsConstructor
    @lombok.Getter
    public static class DrinkUpdate {
        private Long userId;
        private String userName;
        private Double totalSoju;
    }
}