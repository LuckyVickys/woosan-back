package com.luckyvicky.woosan.domain.matching.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberMatchingRequestDTO {
    // 매칭 보드 ID
    private Long matchingId;

    // 회원 ID
    private Long memberId;

    // 수락 여부
    private Boolean isAccepted;

    // 관리 여부
    private Boolean isManaged;
}
