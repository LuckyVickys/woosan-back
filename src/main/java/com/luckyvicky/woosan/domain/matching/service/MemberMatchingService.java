package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MemberMatchingRequestDTO;
import com.luckyvicky.woosan.domain.matching.dto.MemberMatchingResponseDTO;
import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;

import java.util.List;
import java.util.Optional;

public interface MemberMatchingService {

    // MemberMatching 데이터를 생성하는 메서드
    MemberMatchingResponseDTO createMemberMatching(MemberMatchingRequestDTO requestDTO);

    // 매칭 요청 생성
    MemberMatchingResponseDTO applyMatching(MemberMatchingRequestDTO requestDTO);

    // 매칭 상태 업데이트 (수락/거절)
    MemberMatchingResponseDTO updateMatching(Long id, Boolean isAccepted);

    // 모임원 탈퇴
    void leaveMatching(Long matchingId, Long memberId);

    // 회원 강퇴
    void kickMember(Long matchingId, Long memberId);

    // 특정 보드의 모든 멤버 가져오기
    List<MemberMatchingResponseDTO> getMembersByMatchingBoardId(Long matchingId);

    // 특정 보드의 대기 중인 요청 가져오기
    List<MemberMatchingResponseDTO> getPendingRequestsByBoardId(Long matchingId);

    // 매칭 대기 취소
    void cancelMatchingRequest(Long matchingId, Long memberId);

    // 특정 보드의 모든 멤버 매칭 데이터 삭제
    void deleteAllMembersByMatchingBoardId(Long matchingId);

    // 특정 보드의 모든 멤버의 isAccepted 상태 업데이트
    void updateIsAcceptedByMatchingBoardId(Long matchingId, Boolean isAccepted);

    // 특정 보드의 관리자 찾기
    Optional<MemberMatchingResponseDTO> findManagerByMatchingBoardId(Long matchingId);

}
