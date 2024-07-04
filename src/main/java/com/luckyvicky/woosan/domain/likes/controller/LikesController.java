package com.luckyvicky.woosan.domain.likes.controller;

import com.luckyvicky.woosan.domain.likes.dto.ToggleRequestDTO;
import com.luckyvicky.woosan.domain.likes.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikesController {

    private final LikesService likesService;

    /**
     * 추천 토글
     */
    @PostMapping("/toggle")
    public ResponseEntity<Void> toggleLike(@RequestBody ToggleRequestDTO toggleRequestDTO) {
        likesService.toggleLike(toggleRequestDTO.getMemberId(), toggleRequestDTO.getType(), toggleRequestDTO.getTargetId());
        return ResponseEntity.ok().build();
    }


    /**
     * 추천 여부 확인
     */
    @PostMapping("/status")
    public ResponseEntity<Boolean> getLikeStatus(@RequestBody ToggleRequestDTO toggleRequestDTO) {
        boolean liked = likesService.isLiked(toggleRequestDTO.getMemberId(), toggleRequestDTO.getType(), toggleRequestDTO.getTargetId());
        return ResponseEntity.ok(liked);
    }

}
