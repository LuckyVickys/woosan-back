package com.luckyvicky.woosan.domain.report.mapper;

import com.luckyvicky.woosan.domain.member.dto.SignUpReqDTO;
import com.luckyvicky.woosan.domain.member.dto.SignUpResDTO;
import com.luckyvicky.woosan.domain.member.entity.Member;
import com.luckyvicky.woosan.domain.report.dto.ReportDTO;
import com.luckyvicky.woosan.domain.report.entity.Report;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReportMapper {
    ReportDTO reportToReportDTO(Report report);
}
