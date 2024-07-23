package com.luckyvicky.woosan.domain.report.utils;

import com.luckyvicky.woosan.domain.board.repository.jpa.BoardRepository;
import com.luckyvicky.woosan.domain.board.repository.jpa.ReplyRepository;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.messages.repository.MessageRepository;
import com.luckyvicky.woosan.domain.report.dto.ReportDTO;
import com.luckyvicky.woosan.domain.report.entity.Report;
import com.luckyvicky.woosan.domain.report.repository.ReportRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public class ReportServiceUtil {

    public static <T> T getEntityById(JpaRepository<T, Long> repository, Long id, String errorMessage) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException(errorMessage));
    }

    public static void checkExistingReports(ReportRepository reportRepository, Member reporter, String type, Long targetId) {
        boolean reportExists = reportRepository.existsByReporterAndTypeAndTargetId(reporter, type, targetId);
        if (reportExists) {
            throw new IllegalStateException("신고 내역이 존재합니다.");
        }
    }

    public static Member getReportedMember(String type, Long targetId, BoardRepository boardRepository, ReplyRepository replyRepository, MessageRepository messageRepository) {
        switch (type) {
            case "board":
                return getEntityById(boardRepository, targetId, "존재하지 않는 게시판입니다.").getWriter();
            case "reply":
                return getEntityById(replyRepository, targetId, "존재하지 않는 댓글입니다.").getWriter();
            case "message":
                return getEntityById(messageRepository, targetId, "존재하지 않는 메시지입니다.").getSender();
            default:
                throw new IllegalArgumentException("존재하지 않는 신고 유형입니다.");
        }
    }

    public static Report createAndSaveReport(ReportRepository reportRepository, Member reporter, ReportDTO reportDTO, Member reportedMember) {
        Report report = Report.builder()
                .reporter(reporter)
                .type(reportDTO.getType())
                .targetId(reportDTO.getTargetId())
                .complaintReason(reportDTO.getComplaintReason())
                .reportedMember(reportedMember)
                .build();
        return reportRepository.save(report);
    }

}
