package com.luckyvicky.woosan.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyReplyDTO {
    private Long id;  // 댓글 고유번호
    private Long boardId;  // 게시글 고유번호
    private String content;  // 내용
    private int likesCount; // 추천수
    private Long parentId;  // 부모 댓글의 고유번호
    private LocalDateTime regDate;  // 작성 날짜

    private Long writerId;  // 작성자 고유번호

    private String title;
    private String categoryName;
}
