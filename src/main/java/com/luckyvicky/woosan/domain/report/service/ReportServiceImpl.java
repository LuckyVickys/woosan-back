package com.luckyvicky.woosan.domain.report.service;

import com.luckyvicky.woosan.domain.board.repository.BoardRepository;
import com.luckyvicky.woosan.domain.board.repository.ReplyRepository;
import com.luckyvicky.woosan.domain.fileImg.service.FileImgService;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import com.luckyvicky.woosan.domain.report.dto.ReportDTO;
import com.luckyvicky.woosan.domain.report.entity.Report;
import com.luckyvicky.woosan.domain.report.repository.ReportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;
    private final FileImgService fileImgService;

    @Override
    public Long reportAdd(ReportDTO reportDTO) {
        Member reporter = memberRepository.findById(reportDTO.getReporterId()).orElseThrow(() -> new IllegalArgumentException("Reporter not found with id: " + reportDTO.getReporterId()));
        Member reportedMember;

        if(reportDTO.getType().equals("board")) {
            reportedMember = boardRepository.findById(reportDTO.getTargetId()).get().getWriter();
        } else if(reportDTO.getType().equals("reply")) {
            reportedMember = replyRepository.findById(reportDTO.getTargetId()).get().getWriter();
        } else {
            throw new IllegalArgumentException("Invalid report type: " + reportDTO.getType());
        }

        Report report = Report.builder()
                .reporter(reporter)
                .type(reportDTO.getType())
                .targetId(reportDTO.getTargetId())
                .complaintReason(reportDTO.getComplaintReason())
                .reportedMember(reportedMember)
                .build();

        report = reportRepository.save(report); // Save the report and get the persisted entity

        fileImgService.fileUploadMultiple("report", report.getId(), reportDTO.getImages()); // Use the ID of the persisted report
        return null;
    }
}
