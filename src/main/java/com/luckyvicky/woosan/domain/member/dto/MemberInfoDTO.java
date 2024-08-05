package com.luckyvicky.woosan.domain.member.dto;

import lombok.*;
import net.bytebuddy.implementation.bytecode.ShiftRight;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MemberInfoDTO {

    private Long id;
    private String email;
    private String nickname;
    private int point;
    private int nextPoint;
    private String memberType;
    private String level;
    private List<String> profile;

}
