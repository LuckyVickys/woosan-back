package com.luckyvicky.woosan.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WriterDTO {
    private Long id;
    private String nickname;
    private String profileImageUrl; // 작성자의 프로필 이미지 URL

}

