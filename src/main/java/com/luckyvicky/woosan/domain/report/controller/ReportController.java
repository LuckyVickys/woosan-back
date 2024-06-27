package com.luckyvicky.woosan.domain.report.controller;

import com.luckyvicky.woosan.domain.report.dto.ReportDTO;
import com.luckyvicky.woosan.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/add")
    public ResponseEntity<Object> reportAdd(@RequestBody ReportDTO reportDTO) {

//        Long reportId = reportService.reportAdd(reportDTO);
//        return ResponseEntity.ok(reportId);
        try {
            ReportDTO reportAddDTO = reportService.reportAdd(reportDTO);
            return new ResponseEntity<>(reportAddDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}