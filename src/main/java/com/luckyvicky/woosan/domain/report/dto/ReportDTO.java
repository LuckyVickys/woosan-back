package com.luckyvicky.woosan.domain.report.dto;

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

    //신고자
    private Long reporterId;
    private String reporterEmail;
    private String reporterNickName;

    //신고된 사람
    private Long reportedId;
    private String reportedEmail;
    private String reportedNickName;

    //신고 대상
    private String type;
    private Long targetId;

    private String complaintReason;
    private LocalDateTime regDate;
    private Boolean isChecked;

    //사진
    private List<String> filesURL;
    private List<MultipartFile> uploadFiles;

}
