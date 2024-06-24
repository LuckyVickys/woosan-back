package com.luckyvicky.woosan.domain.report.service;

import com.luckyvicky.woosan.domain.member.entity.Member;

public interface ReportService {
    void reportTarget(Long reporterId,String type, Long targetId);
}
