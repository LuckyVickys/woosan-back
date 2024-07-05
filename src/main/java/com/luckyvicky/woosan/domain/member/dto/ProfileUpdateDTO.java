package com.luckyvicky.woosan.domain.member.dto;

import com.luckyvicky.woosan.domain.member.entity.MBTI;
import com.luckyvicky.woosan.domain.member.entity.MemberType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateDTO {
    private long memberId;
    private String nickname;
    private MemberType.Level level;
    private int point;
    private String gender;
    private String location;
    private int age;
    private int height;
    private MBTI mbti;

}
