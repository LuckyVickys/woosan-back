package com.luckyvicky.woosan.domain.member.controller;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.member.dto.ProfileUpdateDTO;
import com.luckyvicky.woosan.domain.member.service.MemberProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberProfileController {

    private final MemberProfileService memberProfileService;

    /**
     * 마이페이지 이동
     * */
    @GetMapping("/{id}")
    public ResponseEntity<ProfileUpdateDTO> getBoardForModification(@PathVariable Long id) {
        ProfileUpdateDTO profileUpdateDTO = memberProfileService.get(id);
        return ResponseEntity.ok(profileUpdateDTO);
    }

    /**
     * 마이페이지 입력 및 수정
     * */
    @PatchMapping("/{id}")
    public ResponseEntity<String> modifyBoard(@PathVariable Long id, @ModelAttribute ProfileUpdateDTO profileUpdateDTO) {
        memberProfileService.update(profileUpdateDTO);
        return ResponseEntity.ok("수정 완료");
    }

}
