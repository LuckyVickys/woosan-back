package com.luckyvicky.woosan.domain.matching.controller;

import com.luckyvicky.woosan.domain.matching.dto.MemberMatchingRequestDTO;
import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;
import com.luckyvicky.woosan.domain.matching.service.MemberMatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member-matching")
public class MemberMatchingController {

    @Autowired
    private MemberMatchingService memberMatchingService;

    //매칭 요청 생성
    @PostMapping
    public MemberMatching createMatching(@RequestBody MemberMatchingRequestDTO requestDTO){
        return memberMatchingService.createMatching(requestDTO);
    }

    //매칭 수락 또는 거부 처리
    @PutMapping("/{id}")
    public MemberMatching updateMatching(@PathVariable Long id, @RequestParam Boolean isAccepted) {
        return memberMatchingService.updateMatching(id, isAccepted);
    }
}
