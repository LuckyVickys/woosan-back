package com.luckyvicky.woosan.domain.member.controller;

import com.luckyvicky.woosan.domain.member.dto.ProfileUpdateDTO;
import com.luckyvicky.woosan.domain.member.service.MemberProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
public class MemberProfileController {
    @Autowired
    private MemberProfileService memberProfileService;


    // 회원정보 수정
    @PostMapping("/profileUpdate")
    public ResponseEntity<?> updateNickname(@RequestBody ProfileUpdateDTO profileUpdateDTO) {
        try {
            System.out.println(profileUpdateDTO);
            memberProfileService.updateProfile(profileUpdateDTO);

            return ResponseEntity.ok("profile changed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 포인트 확인란
    @GetMapping("/ViewPoint")
    public ResponseEntity<?> viewPoint(@RequestBody ProfileUpdateDTO profileUpdateDTO) {
        try {
            System.out.println(profileUpdateDTO);
            memberProfileService.getPointLevel(profileUpdateDTO);
            return ResponseEntity.ok(profileUpdateDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
