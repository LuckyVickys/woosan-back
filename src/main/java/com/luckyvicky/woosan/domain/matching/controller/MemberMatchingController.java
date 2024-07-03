package com.luckyvicky.woosan.domain.matching.controller;

import com.luckyvicky.woosan.domain.matching.dto.MemberMatchingRequestDTO;
import com.luckyvicky.woosan.domain.matching.dto.MemberMatchingResponseDTO;
import com.luckyvicky.woosan.domain.matching.service.MemberMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memberMatching")
@RequiredArgsConstructor
public class MemberMatchingController {

    private final MemberMatchingService memberMatchingService;

    // 매칭 수락 요청 생성
    @PostMapping("/apply")
    public ResponseEntity<?> applyMatching(@RequestBody MemberMatchingRequestDTO requestDTO) {
        try {
            MemberMatchingResponseDTO memberMatching = memberMatchingService.applyMatching(requestDTO);
            return new ResponseEntity<>("매칭 요청이 성공적으로 생성되었습니다.", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("매칭 요청 생성 중 오류가 발생했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 매칭 수락 또는 거부 처리
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMatching(@PathVariable Long id, @RequestParam Boolean isAccepted) {
        try {
            MemberMatchingResponseDTO memberMatching = memberMatchingService.updateMatching(id, isAccepted);
            String message = Boolean.TRUE.equals(isAccepted) ? "매칭 요청을 수락했습니다." : "매칭 요청을 거절했습니다.";
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("매칭 상태 업데이트 중 오류가 발생했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 모임원 탈퇴
    @DeleteMapping("/leave/{id}")
    public ResponseEntity<?> leaveMatching(@PathVariable Long id, @RequestParam Long memberId) {
        try {
            memberMatchingService.leaveMatching(id, memberId);
            return new ResponseEntity<>("모임에서 성공적으로 탈퇴했습니다.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("매칭 탈퇴 중 오류가 발생했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 모임원 강퇴
    @DeleteMapping("/kick/{id}")
    public ResponseEntity<?> kickMember(@PathVariable Long id, @RequestParam Long memberId) {
        try {
            memberMatchingService.kickMember(id, memberId);
            return new ResponseEntity<>("회원이 성공적으로 강퇴되었습니다.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("회원 강퇴 중 오류가 발생했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 모임원의 리스트 가져오기
    @GetMapping("/list/{matchingId}")
    public ResponseEntity<?> getMembers(@PathVariable Long matchingId) {
        try {
            List<MemberMatchingResponseDTO> members = memberMatchingService.getMembersByMatchingBoardId(matchingId);
            return new ResponseEntity<>(members, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("모임원 목록 조회 중 오류가 발생했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 특정 매칭 보드에 대한 가입 대기 중인 요청들을 가져오기
    @GetMapping("/pending/{matchingId}")
    public ResponseEntity<?> getPendingRequestsByBoardId(@PathVariable Long matchingId) {
        try {
            List<MemberMatchingResponseDTO> pendingRequests = memberMatchingService.getPendingRequestsByBoardId(matchingId);
            return new ResponseEntity<>(pendingRequests, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("가입 대기 중인 요청 조회 중 오류가 발생했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 매칭 대기를 취소하는 메서드
    @DeleteMapping("/cancel/{matchingId}")
    public ResponseEntity<?> cancelMatchingRequest(@PathVariable Long matchingId, @RequestParam Long memberId) {
        try {
            memberMatchingService.cancelMatchingRequest(matchingId, memberId);
            return new ResponseEntity<>("매칭 대기가 성공적으로 취소되었습니다.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("잘못된 요청: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("매칭 요청을 찾을 수 없습니다: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("매칭 대기 취소 중 오류가 발생했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
