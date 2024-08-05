package com.luckyvicky.woosan.global.auth.dto;

import com.luckyvicky.woosan.domain.member.entity.MemberType;
import com.luckyvicky.woosan.domain.member.entity.SocialType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomUserInfoDTO {

    private Long id;
    private String email;
    private String password;
    private String nickname;
    private Boolean isActive;
    private int point;
    private int nextPoint;
    private String level;
    private String memberType;
    private String socialType;

    public <T> CustomUserInfoDTO(T id, T email, T memberType, T level) {
    }
}
