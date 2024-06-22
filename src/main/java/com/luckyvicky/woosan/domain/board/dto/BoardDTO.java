package com.luckyvicky.woosan.domain.board.dto;

import com.luckyvicky.woosan.domain.member.dto.MemberDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {

    private Long id;
    private MemberDTO writer;
    private int postType;
    private String title;
    private String content;
    private LocalDateTime regDate;
    private int views;
    private Boolean isDeleted;
    private String categoryName;

}
//private Long writerId;  // 작성자의 ID만 포함 (회원 엔티티와의 연관관계가 아닌 ID로 처리)
//private String nickame;