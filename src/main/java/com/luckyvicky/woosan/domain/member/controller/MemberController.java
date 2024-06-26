package com.luckyvicky.woosan.domain.member.controller;

import com.luckyvicky.woosan.domain.member.dto.*;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.mapper.MemberMapper;
import com.luckyvicky.woosan.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private final MemberMapper mapper;
    private final MemberService memberService;

    // 이메일 중복 체크
    @GetMapping("/email/{email}")
    public ResponseEntity<Object> emailCheck(@PathVariable(value = "email") String email) {
        try {
            return new ResponseEntity(memberService.existEmail(email), HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 닉네임 중복 체크
    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<Object> nicknameCheck(@PathVariable String nickname) {
        try {
            return new ResponseEntity(memberService.existNickname(nickname), HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 회원가입
    @PostMapping("/signUp")
    public ResponseEntity<Object> signUp(@RequestBody SignUpReqDTO reqDTO) {
        Member member = mapper.singUpReqDTOToMember(reqDTO);
        try {
            member = memberService.addMember(member);
            SignUpResDTO memberRes = mapper.memberToSignUpResDTO(member);
            return new ResponseEntity<>(memberRes, HttpStatus.CREATED);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 세션 로그인(스프링시큐리티, jwt토큰 적용 전)
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginReqDTO loginReqDTO) {
        try {
            return new ResponseEntity<>(memberService.login(loginReqDTO), HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 임시비밀번호 메일 전송 및 임시비밀번호 변경
    @PostMapping("/sendEmail")
    public ResponseEntity<Object> sendEmail(@RequestParam("email") String email) {
        try {
            MailDTO dto = memberService.createMailAndChangePw(email);
            memberService.mailSend(dto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 비밀번호 변경 
    @PutMapping("/updatePw")
    public ResponseEntity<Object> updatePw(@RequestBody UpdatePwDTO updatePwDTO) {
        try {
            memberService.updatePassword(updatePwDTO.getEmail(), updatePwDTO.getPassword(), updatePwDTO.getNewPassword());
            return new ResponseEntity<>(true, HttpStatus.CREATED);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
