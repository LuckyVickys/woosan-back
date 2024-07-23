package com.luckyvicky.woosan.domain.report.controller;

import com.luckyvicky.woosan.domain.report.dto.ReportDTO;
import com.luckyvicky.woosan.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/add")
    public ResponseEntity<Void> reportAdd(@ModelAttribute ReportDTO reportDTO) {
        reportService.reportAdd(reportDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}