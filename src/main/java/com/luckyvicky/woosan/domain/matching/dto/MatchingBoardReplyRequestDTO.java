package com.luckyvicky.woosan.domain.matching.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    // 작성자 닉네임
    private String writerNickname;

    // 작성 날짜
    private LocalDateTime regDate;

    // 부모 댓글 고유번호 (대댓글의 경우)
    private Long parentId;

    // 자식 댓글 리스트
    private List<MatchingBoardReplyRequestDTO> childReplies;

    // 매칭 보드 ID
    private Long matchingId;
}