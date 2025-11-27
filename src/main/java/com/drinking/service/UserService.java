package com.drinking.service;

import com.drinking.domain.User;
import com.drinking.domain.DrinkRecord;
import com.drinking.domain.ReactionTest;
import com.drinking.domain.Room;
import com.drinking.dto.UserDtos;
import com.drinking.repository.UserRepository;
import com.drinking.repository.DrinkRecordRepository;
import com.drinking.repository.ReactionTestRepository;
import com.drinking.repository.RoomRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DrinkRecordRepository drinkRecordRepository;
    private final ReactionTestRepository reactionTestRepository;
    private final RoomRepository roomRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void leaveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String roomCode = user.getRoomCode();
        user.setFinishedAt(LocalDateTime.now());
        userRepository.save(user);

        // 남은 참가자 수 확인
        Long remainingCount = userRepository.countByRoomCodeAndFinishedAtIsNull(roomCode);

        // WebSocket으로 참가자 수 변경 알림
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomCode + "/participants",
                new ParticipantUpdate(remainingCount)
        );

        // 1명만 남으면 방 종료 및 데이터 삭제
        if (remainingCount <= 1) {
            cleanupRoom(roomCode);
        }
    }

    @Transactional
    public UserDtos.UserResultResponse finishUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getFinishedAt() == null) {
            user.setFinishedAt(LocalDateTime.now());
        }

        // 시간당 소주 잔 수 계산
        Duration duration = Duration.between(user.getJoinedAt(), user.getFinishedAt());
        double hours = duration.toMinutes() / 60.0;
        double sojuPerHour = hours > 0 ? user.getTotalSoju() / hours : 0;

        // 평균 반응속도 계산
        double avgReactionTime = getAverageReactionTime(userId);

        // AI를 통한 레벨 및 코멘트 생성
        String characterLevel = determineLevel(user.getTotalSoju(), avgReactionTime);
        user.setCharacterLevel(characterLevel);

        // 랭킹 계산
        List<User> users = userRepository.findByRoomCodeOrderByTotalSojuDesc(user.getRoomCode());
        for (int i = 0; i < users.size(); i++) {
            users.get(i).setRanking(i + 1);
        }
        userRepository.saveAll(users);

        String aiComment = generateAIComment(user.getTotalSoju(), avgReactionTime, characterLevel);

        return new UserDtos.UserResultResponse(
                user.getUserName(),
                characterLevel,
                user.getTotalSoju(),
                sojuPerHour,
                user.getRanking(),
                avgReactionTime,
                aiComment
        );
    }

    @Transactional(readOnly = true)
    public List<UserDtos.RankingResponse> getRanking(String roomCode) {
        List<User> users = userRepository.findByRoomCodeOrderByTotalSojuDesc(roomCode);

        return users.stream()
                .map(user -> new UserDtos.RankingResponse(
                        user.getId(),
                        user.getUserName(),
                        user.getTotalSoju(),
                        user.getRanking()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserDtos.RankingResponse> getFinalRanking(String roomCode) {
        List<User> users = userRepository.findByRoomCodeOrderByTotalSojuDesc(roomCode);

        return users.stream()
                .map(user -> new UserDtos.RankingResponse(
                        user.getId(),
                        user.getUserName(),
                        user.getTotalSoju(),
                        user.getRanking()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void cleanupRoom(String roomCode) {
        // 방의 모든 사용자 가져오기
        List<User> users = userRepository.findByRoomCode(roomCode);

        // 각 사용자의 관련 데이터 삭제
        for (User user : users) {
            drinkRecordRepository.deleteById(user.getId());
            reactionTestRepository.deleteById(user.getId());
        }

        // 사용자 삭제
        userRepository.deleteAll(users);

        // 방 삭제
        roomRepository.deleteById(roomCode);

        System.out.println("방 " + roomCode + " 정리 완료");
    }

    private double getAverageReactionTime(Long userId) {
        try {
            ReactionTest test = reactionTestRepository.findById(userId).orElse(null);
            if (test == null) return 0.0;

            List<Integer> times = objectMapper.readValue(
                    test.getReactionTime().isEmpty() ? "[]" : test.getReactionTime(),
                    new TypeReference<List<Integer>>() {}
            );

            return times.stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private String determineLevel(double totalSoju, double avgReactionTime) {
        if (totalSoju >= 10) return "술고래";
        else if (totalSoju >= 7) return "고수";
        else if (totalSoju >= 4) return "중수";
        else if (totalSoju >= 2) return "초보";
        else return "새내기";
    }

    private String generateAIComment(double totalSoju, double avgReactionTime, String level) {
        return String.format("%s 등급입니다! 총 %.1f잔을 마셨고, 평균 반응속도는 %.0fms입니다.",
                level, totalSoju, avgReactionTime);
    }

    @lombok.AllArgsConstructor
    @lombok.Getter
    public static class ParticipantUpdate {
        private Long count;
    }
}