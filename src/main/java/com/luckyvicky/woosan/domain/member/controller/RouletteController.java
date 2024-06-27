package com.luckyvicky.woosan.domain.member.controller;

import com.luckyvicky.woosan.domain.member.dto.RouletteDTO;
import com.luckyvicky.woosan.domain.member.service.RouletteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roulette")
public class RouletteController {
    @Autowired
    private RouletteService rouletteService; // RouletteService를 주입하여 비즈니스 로직을 처리합니다.

    @PostMapping("/updatePoints")
    public ResponseEntity<String> updatePoints(@RequestBody RouletteDTO rouletteDTO) {
        try {
            // 서비스 메서드를 호출하여 멤버의 포인트를 업데이트합니다.
            rouletteService.updateMemberPoints(rouletteDTO);
            // 성공적으로 업데이트된 경우 성공 메시지를 반환
            return ResponseEntity.ok("Points updated successfully");
        } catch (IllegalArgumentException e) {
            // 예외처리
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
