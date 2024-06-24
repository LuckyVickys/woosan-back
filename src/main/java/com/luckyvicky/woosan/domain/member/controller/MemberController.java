package com.luckyvicky.woosan.domain.member.controller;

import com.luckyvicky.woosan.domain.member.dto.SignUpReqDTO;
import com.luckyvicky.woosan.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 이메일 중복 체크
    @GetMapping("email/{email}")
    public ResponseEntity<Object> emailCheck(@PathVariable String email) {
        try {
            return new ResponseEntity(memberService.existEmail(email), HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 닉네임 중복 체크
    @GetMapping("nickname/{nickname}")
    public ResponseEntity<Object> nicknameCheck(@PathVariable String nickname) {
        try {
            return new ResponseEntity(memberService.existNickname(nickname), HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 회원가입
//    @PostMapping("/signUp")
//    public ResponseEntity<Object> signUp(@RequestBody SignUpReqDTO reqDTO) {
//        try {
//            return new ResponseEntity<>(null, HttpStatus.OK);
//        } catch(Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//    }
}
