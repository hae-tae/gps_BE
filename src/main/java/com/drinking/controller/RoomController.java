package com.drinking.controller;

import com.drinking.dto.RoomDtos;
import com.drinking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomService roomService;

    // AI를 통한 랜덤 방 이름 생성 (실제로는 AI API 호출)
    @GetMapping("/generate-name")
    public ResponseEntity<RoomDtos.GenerateRoomNameResponse> generateRoomName() {
        List<String> randomNames = Arrays.asList(
                "ai 생성이름",
                "여기에"
        );

        String randomName = randomNames.get(new Random().nextInt(randomNames.size()));
        return ResponseEntity.ok(new RoomDtos.GenerateRoomNameResponse(randomName));
    }

    @PostMapping
    public ResponseEntity<RoomDtos.RoomResponse> createRoom(
            @RequestBody RoomDtos.CreateRoomRequest request) {
        RoomDtos.RoomResponse response = roomService.createRoom(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/join")
    public ResponseEntity<RoomDtos.RoomResponse> joinRoom(
            @RequestBody RoomDtos.JoinRoomRequest request) {
        RoomDtos.RoomResponse response = roomService.joinRoom(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{roomCode}/start")
    public ResponseEntity<Void> startGame(@PathVariable String roomCode) {
        roomService.startGame(roomCode);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{roomCode}/participants")
    public ResponseEntity<Long> getParticipantCount(@PathVariable String roomCode) {
        Long count = roomService.getParticipantCount(roomCode);
        return ResponseEntity.ok(count);
    }
}