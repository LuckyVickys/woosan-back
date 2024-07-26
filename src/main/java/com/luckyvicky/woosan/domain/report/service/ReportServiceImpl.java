package com.luckyvicky.woosan.domain.report.service;

import com.luckyvicky.woosan.domain.board.entity.Reply;
import com.luckyvicky.woosan.domain.board.repository.jpa.BoardRepository;
import com.luckyvicky.woosan.domain.board.repository.jpa.ReplyRepository;
import com.luckyvicky.woosan.domain.fileImg.service.FileImgService;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.jpa.MemberRepository;
import com.luckyvicky.woosan.domain.messages.entity.Message;
import com.luckyvicky.woosan.domain.messages.repository.MessageRepository;
import com.luckyvicky.woosan.domain.report.dto.ReportDTO;
import com.luckyvicky.woosan.domain.report.dto.TargetDTO;
import com.luckyvicky.woosan.domain.report.entity.Report;
import com.luckyvicky.woosan.domain.report.mapper.ReportMapper;
import com.luckyvicky.woosan.domain.report.repository.ReportRepository;
import com.luckyvicky.woosan.global.annotation.SlaveDBRequest;
import com.luckyvicky.woosan.global.util.CommonUtils;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.global.util.PageResponseDTO;
import com.luckyvicky.woosan.global.util.TargetType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.luckyvicky.woosan.domain.report.utils.ReportServiceUtil.*;

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
    private final CommonUtils commonUtils;


    /**
     * 신고 등록
     */
    @Override
    public ReportDTO reportAdd(ReportDTO reportDTO) {
        Member reporter = getEntityById(memberRepository, reportDTO.getReporterId(), "존재하지 않는 작성자입니다.");
        checkExistingReports(reportRepository, reporter, reportDTO.getType(), reportDTO.getTargetId());
        Member reportedMember = getReportedMember(reportDTO.getType(), reportDTO.getTargetId(), boardRepository, replyRepository, messageRepository);
        Report report = createAndSaveReport(reportRepository, reporter, reportDTO, reportedMember);
        fileImgService.fileUploadMultiple(TargetType.REPORT, report.getId(), reportDTO.getImages());
        return mapToReportDTO(report);
    }

    /**
     * 신고 목록
     */
    @SlaveDBRequest
    @Override
    public PageResponseDTO<ReportDTO> reportList(PageRequestDTO pageRequestDTO) {
        pageRequestDTO.validate();
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("id").descending());
        Page<Report> reports = reportRepository.findAll(pageable);
        List<ReportDTO> reportDTOList = reports.getContent().stream()
                .map(report -> modelMapper.map(report, ReportDTO.class))
                .collect(Collectors.toList());
        return commonUtils.createPageResponseDTO(pageRequestDTO, reportDTOList, reports.getTotalElements());
    }

    /**
     * 신고 상세 보기
     */
    @SlaveDBRequest
    @Override
    public ReportDTO getReport(Long id) {
        Report report = getEntityById(reportRepository, id, "존재하지 않는 신고입니다.");
        List<String> reportFile = fileImgService.findFiles(TargetType.REPORT, report.getId());
        ReportDTO reportDTO = mapToReportDTO(report);
        reportDTO.setFilePathUrl(reportFile);
        return reportDTO;
    }

    /**
     * 신고 확인
     */
    @Override
    public Long checkReport(Long id) {
        Report report = getEntityById(reportRepository, id, "존재하지 않는 신고입니다.");
        report.setIsChecked(true);
        reportRepository.save(report);
        return report.getId();
    }

    /**
     * 신고 대상 이동
     */
    @SlaveDBRequest
    @Override
    public TargetDTO goToTarget(Long id) {
        Report report = getEntityById(reportRepository, id, "존재하지 않는 신고입니다.");

        switch (report.getType()) {
            case TargetType.BOARD:
                return new TargetDTO(TargetType.BOARD, report.getTargetId());
            case TargetType.REPLY:
                Reply reply = getEntityById(replyRepository, report.getTargetId(), "존재하지 않는 댓글 입니다.");
                return new TargetDTO(TargetType.BOARD, reply.getBoard().getId());
            case TargetType.MESSAGE:
                Message message = getEntityById(messageRepository, report.getTargetId(), "존재하지 않는 메시지입니다.");
                return new TargetDTO(TargetType.MESSAGE, message.getId());
            default:
                throw new IllegalArgumentException("존재하지 않는 신고 유형입니다.");
        }
    }

    private ReportDTO mapToReportDTO(Report report) {
        ReportDTO reportDTO = reportMapper.reportToReportDTO(report);
        reportDTO.setReporterId(report.getReporter().getId());
        reportDTO.setReporterNickname(report.getReporter().getNickname());
        reportDTO.setReporteredMemberId(report.getReportedMember().getId());
        reportDTO.setReporteredMemberNickname(report.getReportedMember().getNickname());
        return reportDTO;
    }
}
