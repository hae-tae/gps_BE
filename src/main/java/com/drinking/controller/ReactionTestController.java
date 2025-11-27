package com.drinking.controller;

import com.drinking.dto.ReactionDtos;
import com.drinking.service.ReactionTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class ReactionTestController {

    private final ReactionTestService reactionTestService;

    @PostMapping
    public ResponseEntity<Void> saveReactionTime(
            @RequestBody ReactionDtos.SaveReactionRequest request) {
        reactionTestService.saveReactionTime(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ReactionDtos.ReactionTestResponse> getReactionTest(
            @PathVariable Long userId) {
        ReactionDtos.ReactionTestResponse response = reactionTestService.getReactionTest(userId);
        return ResponseEntity.ok(response);
    }
}