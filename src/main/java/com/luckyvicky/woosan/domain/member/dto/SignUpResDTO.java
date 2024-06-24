package com.luckyvicky.woosan.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class SignUpResDTO {
    private Long id;
    private String email;
    private String nickname;
}
