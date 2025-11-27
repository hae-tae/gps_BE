package com.drinking.controller;

import com.drinking.dto.DrinkDtos;
import com.drinking.service.DrinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drinks")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class DrinkController {

    private final DrinkService drinkService;

    @PostMapping
    public ResponseEntity<DrinkDtos.DrinkRecordResponse> addDrink(
            @RequestBody DrinkDtos.AddDrinkRequest request) {
        DrinkDtos.DrinkRecordResponse response = drinkService.addDrink(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<DrinkDtos.DrinkRecordResponse> getDrinkRecord(
            @PathVariable Long userId) {
        DrinkDtos.DrinkRecordResponse response = drinkService.getDrinkRecord(userId);
        return ResponseEntity.ok(response);
    }
}