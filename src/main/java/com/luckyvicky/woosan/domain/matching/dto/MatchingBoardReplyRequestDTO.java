package com.luckyvicky.woosan.domain.matching.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingBoardReplyRequestDTO {
    // 댓글 고유번호
    private Long id;

    // 댓글 내용
    private String content;

    // 작성자 ID
    private Long writerId;

    // 작성 날짜
    private LocalDateTime regDate;

    // 부모 댓글 고유번호 (답글의 경우)
    private Long parentId;

    // 매칭 보드 ID
    private Long matchingId;
}
