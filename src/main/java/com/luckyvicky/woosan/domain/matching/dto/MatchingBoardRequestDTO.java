package com.luckyvicky.woosan.domain.matching.dto;

import com.luckyvicky.woosan.domain.member.entity.MBTI;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MatchingBoardRequestDTO {

    private Long memberId;
    private int matchingType; // 1: 정기 모임, 2: 번개, 3: 셀프 소개팅
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

    // 셀프 소개팅 관련 필드 추가
    private String location;
    private String introduce;
    private MBTI mbti;
    private String gender;
    private int age;
    private int height;

    //파일 이미지 관련 필드 추가
    private List<MultipartFile> images;
    private List<String> filePathUrl; // 기존 파일 경로 목록 추가

}
