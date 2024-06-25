package com.luckyvicky.woosan.domain.matching.service;

import com.luckyvicky.woosan.domain.matching.dto.MemberMatchingRequestDTO;
import com.luckyvicky.woosan.domain.matching.entity.MemberMatching;

public interface MemberMatchingService {
    MemberMatching createMatching(MemberMatchingRequestDTO requestDTO);
    MemberMatching updateMatching(Long id, Boolean isAccepted);
}
