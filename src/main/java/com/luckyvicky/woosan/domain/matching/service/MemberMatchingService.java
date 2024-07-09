package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MemberMatchingRequestDTO;
import com.luckyvicky.woosan.domain.matching.dto.MemberMatchingResponseDTO;
import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;

import java.util.List;

public interface MemberMatchingService {

    // 매칭 요청 생성
    MemberMatchingResponseDTO applyMatching(MemberMatchingRequestDTO requestDTO);

    // 매칭 수락 또는 거부 처리
    MemberMatchingResponseDTO updateMatching(Long id, Boolean isAccepted);

    // 모임원 탈퇴
    void leaveMatching(Long id, Long memberId);

    // 모임원 강퇴
    void kickMember(Long id, Long memberId);

    // 특정 매칭 보드의 모든 멤버 가져오기
    List<MemberMatchingResponseDTO> getMembersByMatchingBoardId(Long matchingId);

    // 특정 매칭 보드의 대기 중인 요청 가져오기
    List<MemberMatchingResponseDTO> getPendingRequestsByBoardId(Long matchingId);

    // 매칭 대기 취소
    void cancelMatchingRequest(Long matchingId, Long memberId);
}
