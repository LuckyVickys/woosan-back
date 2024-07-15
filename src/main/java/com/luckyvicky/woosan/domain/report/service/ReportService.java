package com.luckyvicky.woosan.domain.report.service;

import com.luckyvicky.woosan.domain.report.dto.ReportDTO;
import com.luckyvicky.woosan.global.util.PageRequestDTO;
import com.luckyvicky.woosan.global.util.PageResponseDTO;

public interface ReportService {
    ReportDTO reportAdd(ReportDTO reportDTO);
    PageResponseDTO<ReportDTO> reportList(PageRequestDTO pageRequestDTO);

    ReportDTO getReport(Long id);
}
