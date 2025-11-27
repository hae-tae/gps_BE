package com.drinking.service;

import com.drinking.domain.ReactionTest;
import com.drinking.domain.User;
import com.drinking.dto.ReactionDtos;
import com.drinking.repository.ReactionTestRepository;
import com.drinking.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReactionTestService {

    private final ReactionTestRepository reactionTestRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void saveReactionTime(ReactionDtos.SaveReactionRequest request) {
        ReactionTest test = reactionTestRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        try {
            List<Integer> times = objectMapper.readValue(
                    test.getReactionTime().isEmpty() ? "[]" : test.getReactionTime(),
                    new TypeReference<List<Integer>>() {}
            );
            List<String> dates = objectMapper.readValue(
                    test.getTestedAt().isEmpty() ? "[]" : test.getTestedAt(),
                    new TypeReference<List<String>>() {}
            );

            times.add(request.getReactionTime());
            dates.add(LocalDateTime.now().toString());

            test.setReactionTime(objectMapper.writeValueAsString(times));
            test.setTestedAt(objectMapper.writeValueAsString(dates));

            reactionTestRepository.save(test);
        } catch (Exception e) {
            throw new RuntimeException("반응속도 저장 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional(readOnly = true)
    public ReactionDtos.ReactionTestResponse getReactionTest(Long userId) {
        ReactionTest test = reactionTestRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("테스트 기록을 찾을 수 없습니다."));

        try {
            List<Integer> times = objectMapper.readValue(
                    test.getReactionTime().isEmpty() ? "[]" : test.getReactionTime(),
                    new TypeReference<List<Integer>>() {}
            );
            List<String> dates = objectMapper.readValue(
                    test.getTestedAt().isEmpty() ? "[]" : test.getTestedAt(),
                    new TypeReference<List<String>>() {}
            );

            double average = times.stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);

            return new ReactionDtos.ReactionTestResponse(times, dates, average);
        } catch (Exception e) {
            throw new RuntimeException("반응속도 조회 중 오류가 발생했습니다.", e);
        }
    }
}
