package com.luckyvicky.woosan.domain.report.service;

import com.luckyvicky.woosan.domain.board.dto.BoardDTO;
import com.luckyvicky.woosan.domain.board.entity.Board;
import com.luckyvicky.woosan.domain.board.entity.Reply;
import com.luckyvicky.woosan.domain.board.repository.jpa.BoardRepository;
import com.luckyvicky.woosan.domain.board.repository.jpa.ReplyRepository;
import com.luckyvicky.woosan.domain.fileImg.service.FileImgService;
import com.luckyvicky.woosan.domain.member.dto.MyBoardDTO;
import com.luckyvicky.woosan.domain.member.dto.MyReplyDTO;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import com.luckyvicky.woosan.domain.messages.entity.Message;
import com.luckyvicky.woosan.domain.messages.repository.MessageRepository;
import com.luckyvicky.woosan.domain.report.dto.ReportDTO;
import com.luckyvicky.woosan.domain.report.dto.TargetDTO;
import com.luckyvicky.woosan.domain.report.entity.Report;
import com.luckyvicky.woosan.domain.report.mapper.ReportMapper;
import com.luckyvicky.woosan.domain.report.repository.ReportRepository;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.global.util.PageResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final FileImgService fileImgService;
    private final ReportMapper reportMapper;
    private final ModelMapper modelMapper;


    @Override
    public ReportDTO reportAdd(ReportDTO reportDTO) {
        Optional<Member> optionalReporter = memberRepository.findById(reportDTO.getReporterId());
        if (!optionalReporter.isPresent()) {
            throw new IllegalArgumentException("존재하지 않는 작성자입니다.");
        }
        Member reporter = optionalReporter.get();

        List<Report> existingReports = reportRepository.findByReporterAndTypeAndTargetId(reporter, reportDTO.getType(), reportDTO.getTargetId());
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
            case "message":
                Optional<Message> optionalMessage = messageRepository.findById(reportDTO.getTargetId());
                if (!optionalMessage.isPresent()) {
                    throw new IllegalArgumentException("존재하지 않는 메시지입니다.");
                }
                reportedMember = optionalMessage.get().getSender();
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


    @Override
    public PageResponseDTO<ReportDTO> reportList(PageRequestDTO pageRequestDTO) {
        pageRequestDTO.validate();
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("id").descending());
        Page<Report> reports = reportRepository.findAll(pageable);

        List<ReportDTO> reportDTOList = reports.getContent().stream()
                .map(report -> modelMapper.map(report, ReportDTO.class))
                .collect(Collectors.toList());

        long totalCount = reports.getTotalElements();

        return PageResponseDTO.<ReportDTO>withAll()
                .dtoList(reportDTOList)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(totalCount)
                .build();
    }

    @Override
    public ReportDTO getReport(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고입니다."));

        List<String> reportFile = fileImgService.findFiles("report", report.getId());

//        report.setIsChecked(true);
//        reportRepository.save(report);

        ReportDTO reportDTO = reportMapper.reportToReportDTO(report);
        reportDTO.setReporterId(report.getReporter().getId());
        reportDTO.setReporterNickname(report.getReporter().getNickname());
        reportDTO.setReporteredMemberId(report.getReportedMember().getId());
        reportDTO.setReporteredMemberNickname(report.getReportedMember().getNickname());
        reportDTO.setFilePathUrl(reportFile);
        return reportDTO;
    }

    @Override
    public Long checkReport(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고입니다."));

        report.setIsChecked(true);
        reportRepository.save(report);
        return report.getId();
    }

    @Override
    public TargetDTO goToTarget(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고입니다."));

        if (report.getType().equals("board")) {
            return new TargetDTO("board", report.getTargetId());

        } else if (report.getType().equals("reply")) {
            Reply reply = replyRepository.findById(report.getTargetId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글 입니다."));
            return new TargetDTO("board", reply.getBoard().getId());

        } else if(report.getType().equals("message")){
            Message message = messageRepository.findById(report.getId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고입니다."));
            return new TargetDTO("message", message.getId());
        }
        return null;
    }


}
