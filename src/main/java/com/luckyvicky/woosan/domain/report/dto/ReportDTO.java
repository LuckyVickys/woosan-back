package com.luckyvicky.woosan.domain.report.dto;

import com.luckyvicky.woosan.domain.member.dto.WriterDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ReportDTO {

    private Long id;

    private Long reporterId;
    private String reporterNickname;

    private Long reporteredMemberId;
    private String reporteredMemberNickname;

    private String type;
    private Long targetId;

    private String complaintReason;
    private LocalDateTime regDate;
    private Boolean isChecked;

    private List<MultipartFile> images;
    private List<String> filePathUrl;
}
