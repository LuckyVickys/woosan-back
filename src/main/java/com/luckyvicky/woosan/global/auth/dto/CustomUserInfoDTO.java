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
    private MemberType memberType;
    private SocialType socialType;
}
