package com.luckyvicky.woosan.domain.report.service;

import com.luckyvicky.woosan.domain.fileImg.repository.FileImgRepository;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.MemberRepository;
import com.luckyvicky.woosan.domain.report.repository.ReportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;

    @Override
    public void reportTarget(Long reporterId, String type, Long targetId) {
        Member reporter = memberRepository.findById(reporterId).get();


    }
}
