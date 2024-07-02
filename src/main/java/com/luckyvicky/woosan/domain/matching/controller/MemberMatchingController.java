package com.luckyvicky.woosan.domain.matching.controller;

import com.luckyvicky.woosan.domain.matching.dto.MemberMatchingRequestDTO;
import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;
import com.luckyvicky.woosan.domain.matching.service.MemberMatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memberMatching")
public class MemberMatchingController {

    @Autowired
    private MemberMatchingService memberMatchingService;

    //매칭 수락 요청 생성
    @PostMapping("/apply")
    public MemberMatching applyMatching(@RequestBody MemberMatchingRequestDTO requestDTO){
        return memberMatchingService.applyMatching(requestDTO);
    }

    //매칭 수락 또는 거부 처리
    @PutMapping("/update/{id}")
    public MemberMatching updateMatching(@PathVariable Long id, @RequestParam Boolean isAccepted) {
        return memberMatchingService.updateMatching(id, isAccepted);
    }

    //모임원 탈퇴
    @DeleteMapping("/leave/{id}")
    public void leaveMatching(@PathVariable Long id, @RequestParam Long memberId ){
        memberMatchingService.leaveMatching(id, memberId);
    }

    //모임원 강퇴
    @DeleteMapping("/kick/{id}")
    public void kickMember(@PathVariable Long id, @RequestParam Long memberId ){
        memberMatchingService.kickMember(id, memberId);
    }

    //모임원의 리스트 가져오기
    @GetMapping("/list/{matchingBoardId}")
    public List<MemberMatching> getMembers(@PathVariable Long matchingBoardId){
        return memberMatchingService.getMembersByMatchingBoardId(matchingBoardId);
    }
}
