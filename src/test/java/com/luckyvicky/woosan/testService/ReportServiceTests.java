package com.luckyvicky.woosan.testService;

import com.luckyvicky.woosan.domain.report.dto.ReportDTO;
import com.luckyvicky.woosan.domain.report.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReportServiceTests {

    @Autowired
    private ReportService reportService;

    @Test
    void testReportInsertTest() {
        Long reportId = 1L;
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setComplaintReason("report ineset test");
        reportDTO.setType("reply");
        reportDTO.setTargetId(1L);
        //사진 업로드 추가
        reportService.reportTarget(reportId, reportDTO);
        System.out.println("=========================================================");
        System.out.println("Report Insert Success");
        System.out.println("=========================================================");

    }

}
