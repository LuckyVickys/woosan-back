package com.luckyvicky.woosan.domain.member.dto;

import com.luckyvicky.woosan.domain.member.entity.MBTI;
import com.luckyvicky.woosan.domain.member.entity.MemberType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateDTO {
    private long memberId;
    private String nickname;
    private String level;

    private int nextPoint;
    private int point;

    private String gender;
    private String location;
    private Integer age;
    private Integer height;
    private MBTI mbti;

    private List<MultipartFile> image;
    private List<String> fileImg;

}
