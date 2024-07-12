package com.luckyvicky.woosan.domain.member.dto;


import lombok.*;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingMyPageDTO {

    private Long memberId;
    private int matchingType;
    private String title;
    private String placeName;
    private LocalDateTime meetDate;
    private int headCount;

}
