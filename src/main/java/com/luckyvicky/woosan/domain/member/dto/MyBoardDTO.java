package com.luckyvicky.woosan.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyBoardDTO {

    private Long id;
    private String title;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
    private int views;
    private int likesCount;
    private String categoryName;
    private int replyCount;
}
