package com.luckyvicky.woosan.domain.matching.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberMatchingResponseDTO {
    // 매칭 ID
    private Long id;

    // 매칭 보드 ID
    private Long matchingId;

    // 회원 ID
    private Long memberId;

    // 수락 여부
    private Boolean isAccepted;

    // 관리 여부
    private Boolean isManaged;
}
