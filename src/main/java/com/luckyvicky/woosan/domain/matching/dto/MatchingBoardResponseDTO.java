package com.luckyvicky.woosan.domain.matching.dto;

import com.luckyvicky.woosan.domain.member.entity.MBTI;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MatchingBoardResponseDTO {

    //정기모임, 번개, 셀프소개팅 관련 공통 필드
    private Long id;
    private Long memberId;
    private int matchingType;
    private String title;
    private String content;
    private String placeName;
    private BigDecimal locationX;
    private BigDecimal locationY;
    private String address;
    private LocalDateTime meetDate;
    private String tag;
    private int headCount;
    private LocalDateTime regDate;
    private int views;
    private Boolean isDeleted;


    //셀프소개팅 관련 추가 필드
    private String location;
    private String introduce;
    private MBTI mbti;
    private String gender;
    private int age;
    private int height;

    //매칭 이미지 주소
    private List<String> filePathUrl;

    //member에서 닉네임 가져오기
    private String nickname;
    //프로필 이미지 주소
    private List<String> profileImageUrl;

}
