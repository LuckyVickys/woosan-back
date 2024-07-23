package com.luckyvicky.woosan.repository;

import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.member.repository.jpa.MemberRepository;
import com.luckyvicky.woosan.domain.report.entity.Report;
import com.luckyvicky.woosan.domain.report.repository.ReportRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class ReportRepositoryTests {


    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void testCreateReport() {
        Member reporter = memberRepository.findById(1L).get();
        Member reportedMember = memberRepository.findById(2L).get();

        Report report = Report.builder()
                .reporter(reporter)
                .type("Test Type")
                .targetId(1L)
                .complaintReason("Test Complaint")
                .regDate(LocalDateTime.now())
                .isChecked(false)
                .reportedMember(reportedMember)
                .build();

        reportRepository.save(report);
        System.out.println("===================================================");
        System.out.println("Report Insert Success");
        System.out.println("===================================================");

    }

    @Test
    void reportFindAllTest() {
        List<Report> report = reportRepository.findAll();
        System.out.println("==================================================");
        System.out.println(report);
        System.out.println("==================================================");
    }

    @Test
    void reportDeleteTest() {
        reportRepository.deleteById(2L);
        System.out.println("===================================================");
        System.out.println("Report Delete Success");
        System.out.println("===================================================");
    }

    @Test
    void reportSelectOne() {
        Report report = reportRepository.findById(5L).get();
        System.out.println("===================================================");
        System.out.println(report);
        System.out.println("===================================================");
    }

    @Test
    void reportChangeContent() {
        Report report = reportRepository.findById(5L).get();
        report.change("changed reason");
        reportRepository.save(report);
        System.out.println("===================================================");
        System.out.println("Report Update Success");
        System.out.println("===================================================");
    }


}
