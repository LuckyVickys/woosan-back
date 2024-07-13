package com.luckyvicky.woosan.domain.member.dto;

import com.luckyvicky.woosan.global.util.PageRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyPageDTO {
    private Long memberId;
    private PageRequestDTO pageRequestDTO;
}
