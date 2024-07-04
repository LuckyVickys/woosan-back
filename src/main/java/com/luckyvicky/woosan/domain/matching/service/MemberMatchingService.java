package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MemberMatchingRequestDTO;
import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;

import java.util.List;

public interface MemberMatchingService {
    MemberMatching createMatching(MemberMatchingRequestDTO requestDTO);

    MemberMatching updateMatching(Long id, Boolean isAccepted);

    void leaveMatching(Long id, Long MemberID);

    void kickMember(Long matchingBoardId, Long memberId);

    List<MemberMatching> getMembersByMatchingBoardId(Long matchingBoardId);
}
