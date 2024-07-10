package com.luckyvicky.woosan.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.bytebuddy.implementation.bytecode.ShiftRight;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class MemberInfoDTO {

    private Long id;
    private String email;
    private String nickname;
    private int point;
    private int nextPoint;
    private String memberType;
    private String level;
}
