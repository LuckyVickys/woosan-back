package com.luckyvicky.woosan.domain.matching.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberMatchingRequestDTO {
    private Long matchingBoardId; // 수정된 부분
    private Long memberId;
    private Boolean isAccepted;
    private Boolean isManaged;
}
