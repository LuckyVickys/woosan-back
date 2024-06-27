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
    private RouletteService rouletteService;

    @PostMapping("/updatePoints")
    public ResponseEntity<String> updatePoints(@RequestBody RouletteDTO rouletteDTO) {
        try {
            rouletteService.updateMemberPoints(rouletteDTO);
            return ResponseEntity.ok("Points updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
