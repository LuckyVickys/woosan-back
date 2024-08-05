package com.luckyvicky.woosan.domain.report.repository;

import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByReporterAndTypeAndTargetId(Member reporter, String type, Long targetId);
    Optional<Report> findById(Long id);
    boolean existsByReporterAndTypeAndTargetId(Member reporter, String type, Long targetId);
}
