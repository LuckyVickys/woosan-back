package com.luckyvicky.woosan.service;

import com.luckyvicky.woosan.domain.report.dto.ReportDTO;
import com.luckyvicky.woosan.domain.report.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class ReportServiceTests {

    @Autowired
    private ReportService reportService;

    @Test
    void testReportInsertTest() throws IOException {

        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setReporterId(1L);
        reportDTO.setComplaintReason("report ineset test");
        reportDTO.setType("reply");
        reportDTO.setTargetId(1L);
        //사진 업로드 추가
        String filePath1 = "C:\\Users\\ho976\\IdeaSnapshots\\OneDrive\\사진\\스크린샷\\20231002_Jang_Won-young_(장원영).jpg";
        String filePath2 = "C:\\Users\\ho976\\IdeaSnapshots\\OneDrive\\사진\\스크린샷\\스크린샷 2024-05-29 163620.png";

        MultipartFile multipartFile1 = new MockMultipartFile("file1", "20231002_Jang_Won-young_(장원영).jpg", "image/jpeg", new FileInputStream(filePath1));
        MultipartFile multipartFile2 = new MockMultipartFile("file2", "스크린샷 2024-05-29 163620.png", "image/png", new FileInputStream(filePath2));
        List<MultipartFile> files = Arrays.asList(multipartFile1, multipartFile2);
        reportDTO.setImages(files);

        reportService.reportAdd(reportDTO);
        System.out.println("=========================================================");
        System.out.println("Report Insert Success");
        System.out.println("=========================================================");

    }

}
