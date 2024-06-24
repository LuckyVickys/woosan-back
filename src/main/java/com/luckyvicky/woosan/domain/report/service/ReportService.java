package com.luckyvicky.woosan.domain.report.service;

import com.luckyvicky.woosan.domain.report.dto.ReportDTO;

public interface ReportService {
    void reportTarget(Long reporterId,ReportDTO reportDTO);

}
