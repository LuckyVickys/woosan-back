package com.luckyvicky.woosan.domain.report.service;

import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.entity.Reply;
import com.luckyvicky.woosan.domain.board.repository.BoardRepository;
import com.luckyvicky.woosan.domain.board.repository.ReplyRepository;
import com.luckyvicky.woosan.domain.fileImg.service.FileImgService;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import com.luckyvicky.woosan.domain.report.dto.ReportDTO;
import com.luckyvicky.woosan.domain.report.entity.Report;
import com.luckyvicky.woosan.domain.report.mapper.ReportMapper;
import com.luckyvicky.woosan.domain.report.repository.ReportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;
    private final FileImgService fileImgService;
    private final ReportMapper reportMapper;


    @Override
    public ReportDTO reportAdd(ReportDTO reportDTO) {
        Optional<Member> optionalReporter = memberRepository.findById(reportDTO.getReporterId());
        if (!optionalReporter.isPresent()) {
            throw new IllegalArgumentException("존재하지 않는 작성자입니다.");
        }
        Member reporter = optionalReporter.get();

        List<Report> existingReports  = reportRepository.findByReporterAndTypeAndTargetId(reporter, reportDTO.getType(), reportDTO.getTargetId());
        if (!existingReports.isEmpty()) {
            throw new IllegalStateException("신고 내역이 존재합니다.");
        }

        Member reportedMember;

        switch (reportDTO.getType()) {
            case "board":
                Optional<Board> optionalBoard = boardRepository.findById(reportDTO.getTargetId());
                if (!optionalBoard.isPresent()) {
                    throw new IllegalArgumentException("존재하지 않는 게시판입니다.");
                }
                reportedMember = optionalBoard.get().getWriter();
                break;
            case "reply":
                Optional<Reply> optionalReply = replyRepository.findById(reportDTO.getTargetId());
                if (!optionalReply.isPresent()) {
                    throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
                }
                reportedMember = optionalReply.get().getWriter();
                break;
            default:
                throw new IllegalArgumentException("존재하지 않는 신고 유형입니다.");
        }

        Report report = Report.builder()
                .reporter(reporter)
                .type(reportDTO.getType())
                .targetId(reportDTO.getTargetId())
                .complaintReason(reportDTO.getComplaintReason())
                .reportedMember(reportedMember)
                .build();

        report = reportRepository.save(report);

        fileImgService.fileUploadMultiple("report", report.getId(), reportDTO.getImages());

        reportDTO = reportMapper.reportToReportDTO(report);
        reportDTO.setReporterId(report.getReporter().getId());
        reportDTO.setReporterNickname(report.getReporter().getNickname());
        reportDTO.setReporteredMemberId(report.getReportedMember().getId());
        reportDTO.setReporteredMemberNickname(report.getReportedMember().getNickname());

        return reportDTO;
    }



}
