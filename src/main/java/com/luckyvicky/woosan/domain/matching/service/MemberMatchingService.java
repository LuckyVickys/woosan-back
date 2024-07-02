package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MemberMatchingRequestDTO;
import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;

import java.util.List;

public interface MemberMatchingService {
    //매칭 요청을 생성하는 메서드
    MemberMatching applyMatching(MemberMatchingRequestDTO requestDTO);
    //매칭 요청을 수락 또는 거부하는 메서드
    MemberMatching updateMatching(Long id, Boolean isAccepted);
    //매칭에서 탈퇴하는 메서드
    void leaveMatching(Long id, Long MemberID);
    //매칭에서 특정 회원을 강퇴하는 메서드
    void kickMember(Long matchingBoardId, Long memberId);
    //특정 매칭 보드에 속한 모든 회원을 가져오는 메서드
    List<MemberMatching> getMembersByMatchingBoardId(Long matchingBoardId);
    // 특정 매칭 보드에 대한 가입 대기 중인 요청들을 가져오는 메서드
    List<MemberMatching> getPendingRequestsByBoardId(Long matchingBoardId);
    // 매칭 대기를 취소하는 메서드
    void cancelMatchingRequest(Long matchingId, Long memberId);
}
