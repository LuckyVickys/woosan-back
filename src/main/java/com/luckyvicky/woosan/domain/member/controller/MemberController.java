package com.luckyvicky.woosan.domain.member.controller;

import com.luckyvicky.woosan.domain.member.dto.*;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.mapper.MemberMapper;
import com.luckyvicky.woosan.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberMapper mapper;
    private final MemberService memberService;

    // 이메일 중복 체크
    @GetMapping("/email/{email}")
    public ResponseEntity<Object> emailCheck(@PathVariable(value = "email") String email) {
        return new ResponseEntity(memberService.existEmail(email), HttpStatus.OK);
    }

    // 닉네임 중복 체크
    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<Object> nicknameCheck(@PathVariable String nickname) {
        return new ResponseEntity(memberService.existNickname(nickname), HttpStatus.OK);
    }

    // 회원가입
    @PostMapping("/signUp")
    public ResponseEntity<Object> signUp(@RequestBody SignUpReqDTO reqDTO) {
        Member member = mapper.singUpReqDTOToMember(reqDTO);
        member = memberService.addMember(member);
        SignUpResDTO memberRes = mapper.memberToSignUpResDTO(member);
        return new ResponseEntity<>(memberRes, HttpStatus.CREATED);
    }

    // 임시비밀번호 메일 전송 및 임시비밀번호 변경
    @PostMapping("/sendEmail")
    public ResponseEntity<Object> sendEmail(@RequestParam("email") String email) {
        MailDTO dto = memberService.createMailAndChangePw(email);
        memberService.mailSend(dto);
        return new ResponseEntity<>("메일 전송 완료", HttpStatus.OK);
    }

    // 비밀번호 변경 
    @PutMapping("/updatePw")
    public ResponseEntity<Object> updatePw(@RequestBody UpdatePwDTO updatePwDTO) {
        memberService.updatePassword(updatePwDTO.getEmail(), updatePwDTO.getPassword(), updatePwDTO.getNewPassword());
        return new ResponseEntity<>(true, HttpStatus.CREATED);
    }

    // 프로필 사진 클릭 시 멤버 정보 보여주기
    @GetMapping("/info")
    public ResponseEntity<Object> memberInfo(@RequestParam("memberId") Long memberId) {
        return new ResponseEntity(memberService.getMemberInfo(memberId), HttpStatus.OK);
    }
}
