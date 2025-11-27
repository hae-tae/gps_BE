package com.drinking.service;

import com.drinking.domain.Room;
import com.drinking.domain.User;
import com.drinking.domain.DrinkRecord;
import com.drinking.domain.ReactionTest;
import com.drinking.dto.RoomDtos;
import com.drinking.repository.RoomRepository;
import com.drinking.repository.UserRepository;
import com.drinking.repository.DrinkRecordRepository;
import com.drinking.repository.ReactionTestRepository;
import com.drinking.exception.RoomNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final DrinkRecordRepository drinkRecordRepository;
    private final ReactionTestRepository reactionTestRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public RoomDtos.RoomResponse createRoom(RoomDtos.CreateRoomRequest request) {
        String roomCode = generateRoomCode();

        Room room = new Room();
        room.setRoomCode(roomCode);
        room.setRoomName(request.getRoomName());
        room.setStatus(0);
        roomRepository.save(room);

        User user = new User();
        user.setRoomCode(roomCode);
        user.setUserName(request.getUserName());
        user.setTotalSoju(0.0);
        userRepository.save(user);

        // 초기 DrinkRecord 생성
        DrinkRecord drinkRecord = new DrinkRecord();
        drinkRecord.setId(user.getId());
        drinkRecordRepository.save(drinkRecord);

        // 초기 ReactionTest 생성
        ReactionTest reactionTest = new ReactionTest();
        reactionTest.setId(user.getId());
        reactionTest.setReactionTime("[]");
        reactionTest.setTestedAt("[]");
        reactionTestRepository.save(reactionTest);

        Long participantCount = userRepository.countByRoomCode(roomCode);

        return new RoomDtos.RoomResponse(
                roomCode,
                room.getRoomName(),
                room.getStatus(),
                participantCount,
                user.getId()
        );
    }

    @Transactional
    public RoomDtos.RoomResponse joinRoom(RoomDtos.JoinRoomRequest request) {
        Room room = roomRepository.findByRoomCode(request.getRoomCode())
                .orElseThrow(() -> new RoomNotFoundException("방을 찾을 수 없습니다."));

        if (room.getStatus() != 0) {
            throw new IllegalStateException("이미 시작된 방입니다.");
        }

        User user = new User();
        user.setRoomCode(request.getRoomCode());
        user.setUserName(request.getUserName());
        user.setTotalSoju(0.0);
        userRepository.save(user);

        // 초기 DrinkRecord 생성
        DrinkRecord drinkRecord = new DrinkRecord();
        drinkRecord.setId(user.getId());
        drinkRecordRepository.save(drinkRecord);

        // 초기 ReactionTest 생성
        ReactionTest reactionTest = new ReactionTest();
        reactionTest.setId(user.getId());
        reactionTest.setReactionTime("[]");
        reactionTest.setTestedAt("[]");
        reactionTestRepository.save(reactionTest);

        Long participantCount = userRepository.countByRoomCode(request.getRoomCode());

        // WebSocket으로 참여 알림
        messagingTemplate.convertAndSend(
                "/topic/room/" + request.getRoomCode(),
                new ParticipantUpdate(participantCount, user.getUserName() + "님이 참여했습니다.")
        );

        return new RoomDtos.RoomResponse(
                room.getRoomCode(),
                room.getRoomName(),
                room.getStatus(),
                participantCount,
                user.getId()
        );
    }

    @Transactional
    public void startGame(String roomCode) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RoomNotFoundException("방을 찾을 수 없습니다."));

        room.setStatus(1);
        roomRepository.save(room);

        // WebSocket으로 게임 시작 알림
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomCode,
                new GameStatusUpdate("STARTED", LocalDateTime.now())
        );
    }

    @Transactional(readOnly = true)
    public Long getParticipantCount(String roomCode) {
        return userRepository.countByRoomCode(roomCode);
    }

    private String generateRoomCode() {
        Random random = new Random();
        String code;
        do {
            code = String.format("%04d", random.nextInt(10000));
        } while (roomRepository.existsByRoomCode(code));
        return code;
    }

    // WebSocket 메시지용 내부 클래스
    @lombok.AllArgsConstructor
    @lombok.Getter
    public static class ParticipantUpdate {
        private Long participantCount;
        private String message;
    }

    @lombok.AllArgsConstructor
    @lombok.Getter
    public static class GameStatusUpdate {
        private String status;
        private LocalDateTime startTime;
    }
}