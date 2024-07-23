package com.luckyvicky.woosan.domain.member.controller;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.member.dto.ProfileUpdateDTO;
import com.luckyvicky.woosan.domain.member.service.MemberProfileService;
import io.lettuce.core.ScriptOutputType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
        return new ResponseEntity<>(profileUpdateDTO, HttpStatus.OK);
    }

    /**
     * 마이페이지 입력 및 수정
     * */
    @PatchMapping
    public ResponseEntity<ProfileUpdateDTO> modifyBoard(@RequestPart(value = "profileUpdateDTO", required = false) ProfileUpdateDTO profileUpdateDTO,
                                                        @RequestPart(value="images", required = false)List<MultipartFile> images) {
        memberProfileService.update(profileUpdateDTO, images);
        return new ResponseEntity<>(profileUpdateDTO, HttpStatus.OK);
    }

}
