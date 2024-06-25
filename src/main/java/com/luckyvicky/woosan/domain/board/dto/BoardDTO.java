package com.luckyvicky.woosan.domain.board.dto;

import com.luckyvicky.woosan.domain.member.dto.MemberDTO;
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
public class BoardDTO {

    private Long id;
    private MemberDTO writer;
    private String title;
    private String content;
    private LocalDateTime regDate;
    private int views;
    private int likesCount;
    private String categoryName;

    private List<MultipartFile> images;
    private List<String> filePathUrl;


}
