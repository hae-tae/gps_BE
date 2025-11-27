package com.drinking.controller;

import com.drinking.dto.UserDtos;
import com.drinking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @PostMapping("/{userId}/finish")
    public ResponseEntity<UserDtos.UserResultResponse> finishUser(
            @PathVariable Long userId) {
        UserDtos.UserResultResponse response = userService.finishUser(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ranking/{roomCode}")
    public ResponseEntity<List<UserDtos.RankingResponse>> getRanking(
            @PathVariable String roomCode) {
        List<UserDtos.RankingResponse> response = userService.getRanking(roomCode);
        return ResponseEntity.ok(response);
    }
}